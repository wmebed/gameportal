<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="home.title"/></title>
    <meta name="menu" content="Home"/>
</head>
<body class="home">

<h3 style="padding-top:0px;">Computer</h2>
<c:choose>
<c:when test="${showCards}">
<div>
<div style="float: left; padding: 2px 2px 2px 2px;"><img src="/gameportal/images/cards_png/${computer[0].imageName}"></img></div>
<div style="float: left; padding: 2px 2px 2px 2px;"><img src="/gameportal/images/cards_png/${computer[1].imageName}"></img></div>
</div>
</c:when>
<c:otherwise>
<div>
<div style="float: left; padding: 2px 2px 2px 2px;"><img src="/gameportal/images/cards_png/b1fv.png"></img></div>
<div style="float: left; padding: 2px 2px 2px 2px;"><img src="/gameportal/images/cards_png/b1fv.png"></img></div>
</div>
</c:otherwise>
</c:choose>

<div style="clear: both;">
<h3 style="padding-top:10px;">River</h2>
<c:choose>
<c:when test="${state != 0}">
<div>
<div style="float: left; padding: 2px 2px 2px 2px;"><img src="/gameportal/images/cards_png/${river[0].imageName}"></img></div>
<div style="float: left; padding: 2px 2px 2px 2px;"><img src="/gameportal/images/cards_png/${river[1].imageName}"></img></div>
<div style="float: left; padding: 2px 2px 2px 2px;"><img src="/gameportal/images/cards_png/${river[2].imageName}"></img></div>
<c:if test="${fn:length(river) >= 4}">
<div style="float: left; padding: 2px 2px 2px 2px;"><img src="/gameportal/images/cards_png/${river[3].imageName}"></img></div>
</c:if>
<c:if test="${fn:length(river) == 5}">
<div style="float: left; padding: 2px 2px 2px 2px;"><img src="/gameportal/images/cards_png/${river[4].imageName}"></img></div>
</c:if>
</div>
</c:when>
<c:otherwise>
<div>
<div style="float: left; padding: 2px 2px 2px 2px;"><img src="/gameportal/images/cards_png/b1fv.png"></img></div>
<div style="float: left; padding: 2px 2px 2px 2px;"><img src="/gameportal/images/cards_png/b1fv.png"></img></div>
<div style="float: left; padding: 2px 2px 2px 2px;"><img src="/gameportal/images/cards_png/b1fv.png"></img></div>
</div>
</c:otherwise>
</c:choose>
</div>


<div>
<div style="clear: both;">
<h3 style="padding-top:10px;">You</h2>
<div style="float: left; padding: 2px 2px 2px 2px;"><img src="/gameportal/images/cards_png/${hand[0].imageName}"></img></div>
<div style="float: left; padding: 2px 2px 2px 2px;"><img src="/gameportal/images/cards_png/${hand[1].imageName}"></img></div>
</div>

<div style="clear: both; padding-top: 10px">
<div style="float: left; padding: 2px 2px 2px 2px;">
<a style="padding: 2px 2px 2px 2px;" class="btn btn-default" href="<c:url value='/poker/main'/>">
    <i class="icon-ok"></i> Deal</a>
</div>
<div style="float: left; padding: 2px 2px 2px 2px;">
<a style="padding: 2px 2px 2px 2px;" class="btn btn-default" href="#" onclick="document.getElementById('form1').submit()">
    <i class="icon-ok"></i> Bet</a>
</div>  
</div>
<c:if test="${not empty hand}">
	<form id="form1" method="POST" action="/gameportal/poker/bet" >

	</form>
</c:if>
</body>
