package my.customtag;

// /**
// * bean:defineのHTMLエンコード用タグライブラリ<br>
// * (bean:define定義の結果に対してHTMLエンコードを実施)
// * @author 7days
// */
// public class MyDefineHtmlEncodeTag extends DefineTag {
//
// public int doEndTag() throws JspException {
//
// // 親クラスの処理を実施
// super.doEndTag();
//
// int inScope = PageContext.PAGE_SCOPE;
//
// try {
// if (toScope != null) {
// inScope = TagUtils.getInstance().getScope(toScope);
// }
// } catch (JspException e) {
// // log.warn("toScope was invalid name so we default to PAGE_SCOPE", e);
// }
//
// // 親クラスの処理結果を取り出し、HTMLエンコードを行って再度格納し直す
// Object obj = pageContext.getAttribute(id, inScope);
// if (obj != null) {
// pageContext.setAttribute(id, nais.lib.StringConvert.text2Html(obj.toString()), inScope);
// }
//
// return (EVAL_PAGE);
// }
//
