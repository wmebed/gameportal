<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="home.title"/></title>
    <meta name="menu" content="Home"/>
</head>
<body class="home">

<h2><fmt:message key="home.heading"/></h2>
<div>
<div style="float: left; padding: 2px 2px 2px 2px;"><img src="/gameportal/images/cards_png/${cards[0].imageName}"></img></div>
<div style="float: left; padding: 2px 2px 2px 2px;"><img src="/gameportal/images/cards_png/${cards[1].imageName}"></img></div>
<div style="float: left; padding: 2px 2px 2px 2px;"><img src="/gameportal/images/cards_png/${cards[2].imageName}"></img></div>
<div style="float: left; padding: 2px 2px 2px 2px;"><img src="/gameportal/images/cards_png/${cards[3].imageName}"></img></div>
<div style="float: left; padding: 2px 2px 2px 2px;"><img src="/gameportal/images/cards_png/${cards[4].imageName}"></img></div>

<div style="clear: both;">
<div style="float: left; padding: 2px 2px 2px 2px;">
<a style="padding: 2px 2px 2px 2px;" class="btn btn-default" href="<c:url value='/poker/main'/>">
    <i class="icon-ok"></i> Deal</a>
</div>
<div style="float: left; padding: 2px 2px 2px 2px;">
<a style="padding: 2px 2px 2px 2px;" class="btn btn-default" href="<c:url value='/poker/bet'/>">
    <i class="icon-ok"></i> Bet</a>
</div>
</div>
</body>
