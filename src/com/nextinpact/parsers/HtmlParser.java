package com.nextinpact.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.SimpleHtmlSerializer;
import org.htmlcleaner.TagNode;

import com.nextinpact.models.INPactComment;
import com.nextinpact.models.INpactArticle;
import com.nextinpact.models.INpactArticleDescription;

import android.text.Html;
import android.util.Log;

public class HtmlParser {
	TagNode rootNode;
	HtmlCleaner cleaner;
	SimpleHtmlSerializer htmlSerializer;

	public HtmlParser(URL htmlPage) throws IOException {

		cleaner = new HtmlCleaner();
		rootNode = cleaner.clean(htmlPage);
	}

	public HtmlParser(InputStream htmlPage) throws IOException {
		cleaner = new HtmlCleaner();
		rootNode = cleaner.clean(htmlPage);

		HtmlCleaner cleaner = new HtmlCleaner();
		CleanerProperties props = cleaner.getProperties();
		htmlSerializer = new SimpleHtmlSerializer(props);

	}

	/**
	 * Récupère les commentaires
	 * @return
	 */
	public List<INPactComment> getComments() {
		/*
		 * <div class="actu_comm" id="c4500881">
		 * 
		 * <span class="actu_comm_author"> DarKCallistO <span>le 17/03/2013 à
		 * 14:12:55</span> <span class="actu_comm_num">#1</span> </span>
		 * 
		 * <div class="actu_comm_content"> Ah bah ENFIN ! <br> <br> M'enfin <br>
		 * <br> (Indice : elles viennent bien De quelque part..) </div>
		 * 
		 * </div>
		 */

		List<INPactComment> comments = new ArrayList<INPactComment>();

		for (TagNode htmlComment : rootNode.getElementsByAttValue("class",
				"actu_comm ", true, true)) {

			TagNode actu_comm_author = getFirstElementByAttValue(htmlComment,
					"class", "actu_comm_author");
			if (actu_comm_author == null)
				continue;

			TagNode actu_comm_content = getFirstElementByAttValue(htmlComment,
					"class", "actu_comm_content");
			if (actu_comm_content == null)
				continue;

			TagNode quote_bloc = getFirstElementByAttValue(htmlComment,
					"class", "quote_bloc");

			String commentDate = null;
			TagNode span = getFirstElementByName(actu_comm_author, "span");
			if(span != null) {
				commentDate = Html.fromHtml(span.getText().toString()).toString();
			}

			String commentID = null;
			TagNode actu_comm_num = getFirstElementByAttValue(actu_comm_author,
					"class", "actu_comm_num");
			if(actu_comm_num != null) {
				commentID = Html.fromHtml(actu_comm_num.getText().toString()).toString();
			}

			for (TagNode child: actu_comm_author.getChildTags()) {
				actu_comm_author.removeChild(child);
			}
			String auth = Html.fromHtml(actu_comm_author.getText().toString()).toString();

			String content = getStringWithLineBreaks(actu_comm_content);
			String quote = null;

			if (quote_bloc != null) {
				quote = getStringWithLineBreaks(quote_bloc);
				content = content.substring(quote.length(), content.length());
			}

			INPactComment comment = new INPactComment();

			comment.author = auth;
			comment.commentDate = commentDate;
			comment.commentID = commentID;
			comment.quote = quote;
			comment.content = content;

			comments.add(comment);

		}

		return comments;
	}

	public String getStringWithLineBreaks(TagNode node) {
		String content = null;

		try {
			content = htmlSerializer.getAsString(node);
		} catch (IOException e) {

			return null;
		}
		content = content.replaceAll("<br />", "____");
		content = Html.fromHtml(content).toString();
		content = content.replaceAll("____",
				System.getProperty("line.separator"));

		return content;

	}

	/**
	 * Contenu d'un article
	 * @return
	 */
	public INpactArticle getArticleContent() {

		/*
		 * <article>
		 * 
		 * <header class="actu_title">
		 * 
		 * <div class="actu_title_icons_collumn"> <img class="actu_icons"
		 * title="Hadopi" alt="Hadopi" width="32px" height="32px"
		 * src="/images/clair/categories/droit/hadopi@2x.png"/> </div>
		 * 
		 * <div class="actu_title_collumn"> <h1>Les coûts de la Hadopi à
		 * l’honneur de Capital sur M6</h1> <span class="actu_sub_title">Argent,
		 * trop cher ?</span> </div>
		 * 
		 * </header>
		 * 
		 * <div class="actu_content" data_id="78299"> <p class="actu_chapeau">Ce
		 * soir l&rsquo;&eacute;mission&nbsp;<a
		 * href="http://www.m6.fr/emission-capital/" target="_blank">Capital sur
		 * M6</a>&nbsp;concentre son attention sur le gaspillage de l'argent
		 * public. &Agrave; cette occasion, l&rsquo;&eacute;mission va aborder
		 * le sujet de la loi Hadopi.</p><p><a class="fancyimg"
		 * href="http://static.nextinpact.com/images/bd/news/129249.png"
		 * rel="group_fancy"><img
		 * style="display: block; margin-left: auto; margin-right: auto;"
		 * src="http://static.nextinpact.com/images/bd/news/medium-129249.png"
		 * alt="capital M6"/></a></p><p>&nbsp;</p><p>L&rsquo;&eacute;mission
		 * Capital sur M6 se penche ce soir sur l&rsquo;utilisation de
		 * l&rsquo;argent public. &laquo;<em> L'&Eacute;tat d&eacute;pense-t-il
		 * correctement l'argent de nos imp&ocirc;ts ? C'est une question que
		 * nous nous posons tous tr&egrave;s r&eacute;guli&egrave;rement. Alors
		 * que le gouvernement demande de gros efforts aux Fran&ccedil;ais, la
		 * r&eacute;daction de Capital a voulu savoir comment sont
		 * utilis&eacute;es les sommes r&eacute;colt&eacute;es aupr&egrave;s des
		 * contribuables. L'argent est-il jet&eacute; par les fen&ecirc;tres ?
		 * L'administration fait-elle les m&ecirc;mes efforts que nous ?</em>
		 * &raquo; se demande l&rsquo;&eacute;quipe de
		 * Capital.</p><p>&nbsp;</p><p>&Agrave; cette occasion,
		 * l&rsquo;&eacute;mission pr&eacute;sent&eacute;e par Thomas Sotto va
		 * consacrer une dizaine de minutes &agrave; la loi Hadopi. Celle-ci a
		 * mobilis&eacute; plus de 30 millions d&rsquo;euros de subventions
		 * publiques.&nbsp;Pour 2013, la Hadopi devrait percevoir 8,5 millions
		 * d'euros, mais elle anticipe toujours <a href=
		 * "http://www.nextinpact.com/news/77239-hadopi-103-millions-deuros-charges-pour-85-m-subvention-en-2013.htm"
		 * itemprop="news" rel=
		 * "77239-hadopi-103-millions-deuros-charges-pour-85-m-subvention-en-2013"
		 * class="pci_ref" target="_blank">plus de 10 millions d'euros de
		 * d&eacute;penses</a>. Un budget que le minist&egrave;re de la Culture
		 * avoue &ecirc;tre dans l'<a href=
		 * "http://www.nextinpact.com/news/77913-le-ministere-culture-incapable-detailler-budget-hadopi.htm"
		 * itemprop="news"
		 * rel="77913-le-ministere-culture-incapable-detailler-budget-hadopi"
		 * class="pci_ref" target="_blank">incapacit&eacute; de
		 * d&eacute;tailler</a>, et qui pourrait m&ecirc;me exploser si on y
		 * ajoute<a href=
		 * "http://www.nextinpact.com/news/77743-hadopi-free-veut-savoir-qui-doit-indemniser-ses-frais.htm"
		 * itemprop="news"
		 * rel="77743-hadopi-free-veut-savoir-qui-doit-indemniser-ses-frais"
		 * class="pci_ref" target="_blank">l'indemnisation des fournisseurs
		 * d'acc&egrave;s</a>.</p><p>&nbsp;</p><p>En tout, seuls trois jugements
		 * ont &eacute;t&eacute; prononc&eacute;s : <a href=
		 * "http://www.nextinpact.com/news/76967-hadopi-premier-jugement-relaxe.htm"
		 * itemprop="news" rel="76967-hadopi-premier-jugement-relaxe"
		 * class="pci_ref" target="_blank">une relaxe</a>, une <a href=
		 * "http://www.nextinpact.com/news/77604-hadopi-condamne-pour-seul-film-flashe-plus-100-fois.htm"
		 * itemprop="news"
		 * rel="77604-hadopi-condamne-pour-seul-film-flashe-plus-100-fois"
		 * class="pci_ref" target="_blank">dispense de peine</a> et <a href=
		 * "http://www.nextinpact.com/news/74364-hadopi-condamne-pour-seul-titre-flashe-150-fois.htm"
		 * itemprop="news"
		 * rel="74364-hadopi-condamne-pour-seul-titre-flashe-150-fois"
		 * class="pci_ref" target="_blank">150 euros de contravention</a>. Une
		 * loi aux effets suppos&eacute;s toujours bien maigres puisque la
		 * Hadopi a expliqu&eacute; - <a
		 * href="http://www.laquadrature.net/wiki/Etudes_sur_le_partage_de_fichiers"
		 * target="_blank">comme d&rsquo;autres &eacute;tudes</a> - que ceux qui
		 * t&eacute;l&eacute;chargent le plus sont aussi ceux qui
		 * ach&egrave;tent le plus de biens culturels. Le reportage, dans lequel
		 * nous intervenons, est programm&eacute; en fin
		 * d&rsquo;&eacute;mission.</p> </div>
		 * 
		 * <footer class="actu_footer"> Rédigé par <a href="#">Marc Rees</a> (6
		 * 695 lectures) <br/> Le dimanche 17 mars 2013 à 14:06 </footer>
		 * 
		 * <div class="actu_social"> </div>
		 * 
		 * </article>
		 */

		TagNode article = getFirstElementByName(rootNode, "article");
		if (article == null)
			return null;

		TagNode actu_title_collumn = getFirstElementByAttValue(article,
				"class", "actu_title_collumn");
		if (actu_title_collumn == null)
			return null;

		TagNode actu_title_icons_collumn = getFirstElementByAttValue(article,
				"class", "actu_title_icons_collumn");
		if (actu_title_icons_collumn != null) {
			actu_title_icons_collumn.getParent().removeChild(
					actu_title_icons_collumn);
		}

		TagNode htmlH1Element = getFirstElementByName(actu_title_collumn, "h1");
		if (htmlH1Element == null)
			return null;

		String title = htmlH1Element.getText().toString();

		TagNode actu_content = getFirstElementByAttValue(rootNode, "class",
				"actu_content");
		if (actu_content == null)
			return null;

		for (TagNode link : actu_content.getElementsByName("a", true)) {
			link.removeAttribute("href");
		}

		try {
			INpactArticle content = new INpactArticle();
			content.Title = Html.fromHtml(title).toString();
			content.Content = htmlSerializer.getAsString(article);
			return content;
		} catch (IOException e) {

			Log.e("WTF", "" + e.getMessage());
		}

		catch (Exception e) {
			Log.e("WTF", "" + e.getMessage());
		}

		return null;

	}

	/**
	 * Résumés de l'ensemble des articles (vue générale)
	 * @return
	 */
	public List<INpactArticleDescription> getArticles() {
		/*
		 * <article> <div> <div> <a
		 * href="/news/78299-les-couts-hadopi-a-l-honneur-capital-sur-m6.htm">
		 * <img
		 * data-src="http://static.nextinpact.com/images/bd/dedicated/78299.jpg"
		 * alt="Les coûts de la Hadopi à l’honneur de Capital sur M6" src=
		 * "data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw=="
		 * onload="lzld(this)" onerror="lzld(this)" class=""/> </a> </div> <div>
		 * <h1> <a
		 * href="/news/78299-les-couts-hadopi-a-l-honneur-capital-sur-m6.htm">
		 * Les coûts de la Hadopi à l’honneur de Capital sur M6</a> </h1> <p>
		 * <span class="date_pub">14:06</span> - Argent, trop cher ? </p> <a
		 * class="notif_link"
		 * href="/news/78299-les-couts-hadopi-a-l-honneur-capital-sur-m6.htm?vc=1"
		 * > <span>35</span> <span class="sprite sprite-ico-commentaire"></span>
		 * </a> </div> </div> </article>***NEW*** <article
		 * data-datePubli="04/05/2013 15:33:13"> <div> <div> <a href=
		 * "/news/79579-il-y-a-35-ans-premier-spam-histoire-navait-que-600-destinataires.htm"
		 * > <img
		 * data-src="http://static.nextinpact.com/images/bd/dedicated/79579.jpg"
		 * alt=
		 * "Il y a 35 ans, le premier spam de l'histoire n'avait que 600 destinataires"
		 * src=
		 * "data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw=="
		 * onload="lzld(this)" onerror="lzld(this)" class=""/> </a> </div> <div>
		 * <h1> <a href=
		 * "/news/79579-il-y-a-35-ans-premier-spam-histoire-navait-que-600-destinataires.htm"
		 * class="ui-link"> Il y a 35 ans, le premier spam de l'histoire n'avait
		 * que 600 destinataires</a> </h1> <p> <span
		 * class="date_pub">15:33</span> - Dire que certains pensent encore que
		 * le champ CC c&#39;est... Copie Cach&#233;e </p> <a class="notif_link"
		 * class="ui-link" href=
		 * "/news/79579-il-y-a-35-ans-premier-spam-histoire-navait-que-600-destinataires.htm?vc=1"
		 * > <span>33</span> <span class="sprite sprite-ico-commentaire"></span>
		 * </a> </div> </div> </article> <article>
		 */

		List<TempClass> days = new ArrayList<TempClass>();
		for (TagNode htmlSpan : rootNode.getElementsByAttValue("class",
				"actu_separator_date", true, true)) {

			TempClass temp = new TempClass();
			temp.index = htmlSpan.getParent().getChildIndex(htmlSpan);
			// Date qui sera affichée
			HtmlCleaner monHtmlCleaner = new HtmlCleaner();
			temp.value = monHtmlCleaner.getInnerHtml(htmlSpan);
						
			days.add(temp);
		}

		List<INpactArticleDescription> articles = new ArrayList<INpactArticleDescription>();

		for (TagNode htmlArticle : rootNode.getElementsByName("article", true)) {

			int childIndex = htmlArticle.getParent().getChildIndex(htmlArticle);

			TagNode img = getFirstElementByName(htmlArticle, "img");
			if (img == null)
				continue;

			TagNode h1 = getFirstElementByName(htmlArticle, "h1");
			if (h1 == null)
				continue;

			TagNode a = getFirstElementByName(h1, "a");
			if (a == null)
				continue;

			TagNode p = getFirstElementByName(htmlArticle, "p");
			if (p == null)
				continue;

			TagNode notif_link = getFirstElementByAttValue(htmlArticle,
					"class", "notif_link ui-link");
			if (notif_link == null) {
				TagNode temp = getFirstElementByAttValue(htmlArticle, "class",
						"sprite sprite-ico-commentaire");
				notif_link = (TagNode) temp.getParent().getChildren().get(0);
			}

			String imgUrl = img.getAttributeByName("data-src");
			if (imgUrl == null)
				imgUrl = img.getAttributeByName("src");
			String url = a.getAttributeByName("href");
			String title = a.getText().toString();
			String subTitleWithDate = p.getText().toString();

			subTitleWithDate = Html.fromHtml(subTitleWithDate).toString();

			String date = "";
			String subTitle = "";

			if (subTitleWithDate.length() > 7) {
				date = subTitleWithDate.substring(0, 5);
				subTitle = subTitleWithDate.substring(5,
						subTitleWithDate.length());
			}

			else
				subTitle = subTitleWithDate;

			String coms = notif_link == null ? "0" : notif_link.getText()
					.toString();

			INpactArticleDescription article = new INpactArticleDescription();
			article.imgURL = imgUrl;
			article.setUrl(url);
			article.title = Html.fromHtml(title).toString();
			article.date = date;
			article.subTitle = subTitle;
			article.numberOfComs = coms;

			TempClass t = getDayForArticle(childIndex, days);
			if (t != null) {
				article.day = t.value;
				article.section = t.index;
			}

			articles.add(article);

		}

		return articles;

	}

	class TempClass {
		int index;
		String value;
	}

	public static TempClass getDayForArticle(int articleIndex,
			List<TempClass> days) {
		TempClass value = null;

		for (int i = days.size() - 1; i > -1; i--) {
			TempClass temp = days.get(i);
			if (articleIndex < temp.index)
				continue;

			if (articleIndex > temp.index) {
				value = temp;
				break;
			}

		}

		return value;
	}

	/**
	 * Premier élement par valeur d'attribut
	 * @param node
	 * @param attrName Nom de l'attribut
	 * @param attrValue Valeur de l'attribut
	 * @return Tagnode
	 */
	public static TagNode getFirstElementByAttValue(TagNode node,
			String attrName, String attrValue) {
		TagNode[] nodes = node.getElementsByAttValue(attrName, attrValue, true,
				true);
		if (nodes.length == 0)
			return null;

		return nodes[0];
	}

/**
 * Premier élément par nom
 * @param node
 * @param name Nom
 * @return Tagnode
 */
	public static TagNode getFirstElementByName(TagNode node, String name) {
		TagNode[] nodes = node.getElementsByName(name, true);
		if (nodes.length == 0)
			return null;

		return nodes[0];
	}

}
