<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template name="character-header">
  <xsl:param name="headerKey" />
  <xsl:param name="genderId" />
  <xsl:param name="raceId" />
  <xsl:param name="classId" />
  <xsl:param name="realm" />
  <xsl:param name="lang" />
  <xsl:param name="battleGroup" />
  <xsl:param name="level" />
  <xsl:param name="points" />
  <xsl:param name="title" />
  <xsl:param name="guildName" />
  <xsl:param name="guildUrl" />
  <xsl:param name="race" />
  <xsl:param name="class" />
  <xsl:param name="name" />
  <xsl:param name="isPropass" select = "'false'" />
  <xsl:param name="pathPropass" select = "''" />

<script type="text/javascript">
	rightSideImage = "character";
	//changeRightSideImage(rightSideImage);
	theCharUrl = "<xsl:value-of select="page/characterInfo/character/@charUrl" />";
</script>
<div class="profile-header">

 <div class="profile-avatar">
 <xsl:if test="$points"><xsl:attribute name="class">profile-avatar profile-achieve</xsl:attribute></xsl:if>
  <div class="profile-placeholder">
  <a class="avatar-position">
      <xsl:choose>
        <xsl:when test="$level &lt; 60">
	      <img src = "/images/portraits/wow-default/{$genderId}-{$raceId}-{$classId}.gif" />
	    </xsl:when>
      <xsl:when test="$level &lt; 70">
	      <img src = "/images/portraits/wow/{$genderId}-{$raceId}-{$classId}.gif" />
	    </xsl:when>
      <xsl:when test="$level &lt; 80">
	      <img src = "/images/portraits/wow-70/{$genderId}-{$raceId}-{$classId}.gif" />
	    </xsl:when>
	    <xsl:otherwise>
	      <img src = "/images/portraits/wow-80/{$genderId}-{$raceId}-{$classId}.gif" />
	    </xsl:otherwise>
	</xsl:choose>
 </a>
   <p>
<!-- insert flash level -->

<div id="level">
<xsl:if test="$points">
<div class="points">
 <xsl:attribute name="class">
  <xsl:choose>
   <xsl:when test="$isPropass = 'true'">points points-propass</xsl:when>
   <xsl:when test="character/@factionId = 0">points points-alliance</xsl:when>
   <xsl:otherwise>points points-horde</xsl:otherwise>
  </xsl:choose>
  </xsl:attribute>
<a href="character-achievements.xml?{character/@charUrl}"><xsl:value-of select="$points" /></a>
</div>
</xsl:if>

<xsl:call-template name="flash">
<xsl:with-param name="id" select="'leveltext'"/>
<xsl:with-param name="src" select="concat('/images/',$lang,'/flash/level.swf')"/>
<xsl:with-param name="base" select="concat('/images/',$lang,'/flash')"/>
<xsl:with-param name="wmode" select="'transparent'"/>
<xsl:with-param name="width" select="'38'"/>
<xsl:with-param name="height" select="'38'"/>
<xsl:with-param name="quality" select="'best'"/>
<xsl:with-param name="flashvars" select="concat('charLevel=', $level,'&amp;pts=',$points)"/>
<xsl:with-param name="noflash" select="concat('&lt;div class=level-noflash&gt;',$level,'&lt;em&gt;',$level,'&lt;/em&gt;&lt;/div&gt;')"/>
</xsl:call-template>
   </div>

   </p>
  </div>
 </div>



 <div class="flash-profile" id="profile">
<!-- insert flash character info -->
<script type="text/javascript">
	var guildUrl="<xsl:value-of select="concat('guild-info.xml?',$guildUrl)"/>";



	if(Browser.ie &amp;&amp; region == "KR")
		guildUrl = unescape(guildUrl);//.replace(/&amp;/g, "%26");
</script>
<xsl:variable name="textRace" select="$loc/strs/races/str[@id=concat('armory.races.race.', $raceId,'.', $genderId)]" />
<xsl:variable name="textClass" select="$loc/strs/classes/str[@id=concat('armory.classes.class.', $classId,'.', $genderId)]" />
<xsl:variable name="textLevel" select="$loc/strs/character/str[@id='character-level-sheet']" />



<div class="character-details">
	<div class="character-outer">
		<table><tr><td>
			<xsl:if test="$lang!='ko_kr'">
			<h1><span style="font-size: 16px;"><xsl:value-of select="/page/characterInfo/character/@prefix" /><xsl:comment/></span></h1>
			<h2 style="white-space:nowrap"><xsl:value-of select="/page/characterInfo/character/@name" /><span style="font-size:16px;font-weight:normal">&#160;<xsl:value-of select="/page/characterInfo/character/@suffix" /></span></h2>
			<h3><xsl:choose>
				  <xsl:when test = "$isPropass != 'true'"><xsl:value-of select="$guildName" /></xsl:when>
				  <xsl:otherwise><xsl:value-of select = "$pathPropass/@name" /></xsl:otherwise>
				  </xsl:choose></h3>
			<h4>
				<xsl:call-template name="stringorder">
					<xsl:with-param name="datainsert3" select="$textRace"/>
					<xsl:with-param name="datainsert4" select="$textClass"/>
					<xsl:with-param name="datainsert2" select="$level"/>
					<xsl:with-param name="datainsert1" select="$textLevel"/>
					<xsl:with-param name="orderid" select="'armory.order.character-string'"/>
				</xsl:call-template>
			</h4>
			</xsl:if>
		</td></tr></table>
	</div>
	<div class="character-clone">
		<table><tr><td>
			<h1><span style="font-size: 16px; color: #fff7d2"><xsl:value-of select="/page/characterInfo/character/@prefix" /><xsl:comment/></span></h1>
			<h2><xsl:value-of select="/page/characterInfo/character/@name" /><span style="font-size:16px;font-weight:normal;color:#fff7d2">&#160;<xsl:value-of select="/page/characterInfo/character/@suffix" /></span></h2>
			<h3><xsl:choose>
				  <xsl:when test = "$isPropass != 'true'"><a href="guild-info.xml?{$guildUrl}"><xsl:value-of select="$guildName" /></a></xsl:when>
				  <xsl:otherwise><a href = "team-info.xml?{$pathPropass/@url}" class="team"><xsl:value-of select = "$pathPropass/@name" /></a></xsl:otherwise>
				  </xsl:choose>
			</h3>
			<h4>
				<xsl:call-template name="stringorder">
					<xsl:with-param name="datainsert3" select="$textRace"/>
					<xsl:with-param name="datainsert4" select="$textClass"/>
					<xsl:with-param name="datainsert2" select="$level"/>
					<xsl:with-param name="datainsert1" select="$textLevel"/>
					<xsl:with-param name="orderid" select="'armory.order.character-string'"/>
				</xsl:call-template>
			</h4>
		</td></tr></table>
	</div>
	<!-- realm & battleGroup Link Localization -->
	<div>
		<div style="position: absolute; margin: 0px 0 0 0px; width: 700px; ">
			<xsl:choose>
				<xsl:when test = "$isPropass != 'false'">
					<a class="linksmallframe" href="{$loc/strs/propass/str[@id='forum.link']}" target="_blank"><div class="smallframe-a"></div><div class="smallframe-b staticTip"><xsl:attribute name="onMouseOver"> setTipText("<xsl:value-of select="$loc/strs/propass/str[@id='forum.link.mouseover']"/>");</xsl:attribute><xsl:value-of select="$realm" /></div><div class="smallframe-icon"><div class="reldiv"><div class="smallframe-realm"/></div></div><div class="smallframe-c"></div></a>
					<a class="linksmallframe" href="{$loc/strs/propass/str[@id='forum.link']}" target="_blank"><div class="smallframe-a"></div><div class="smallframe-b staticTip"><xsl:attribute name="onMouseOver"> setTipText("<xsl:value-of select="$loc/strs/propass/str[@id='forum.link.mouseover']"/>");</xsl:attribute><xsl:value-of select="$battleGroup" /></div><div class="smallframe-icon"><div class="reldiv"><div class="smallframe-battlegroup"/></div></div><div class="smallframe-c"></div></a>
				</xsl:when>
				<xsl:when test="$lang='ko_kr'">
					<a class="linksmallframe staticTip" href="http://www.worldofwarcraft.co.kr/news/server/index.do" target="_blank"><xsl:attribute name="onMouseOver"> setTipText("<xsl:value-of select="$loc/strs/unsorted/str[@id='armory.icon.realm']"/>");</xsl:attribute><div class="smallframe-a"></div><div class="smallframe-b"><xsl:value-of select="$realm" /></div><div class="smallframe-icon"><div class="reldiv"><div class="smallframe-realm"/></div></div><div class="smallframe-c"></div></a>
					<a class="linksmallframe staticTip" href="http://www.worldofwarcraft.co.kr/guide/battleground/battlegroups.html" target="_blank"><xsl:attribute name="onMouseOver"> setTipText("<xsl:value-of select="$loc/strs/unsorted/str[@id='armory.forum.link.battleGroup']"/>");</xsl:attribute><div class="smallframe-a"></div><div class="smallframe-b"><xsl:value-of select="$battleGroup" /></div><div class="smallframe-icon"><div class="reldiv"><div class="smallframe-battlegroup"/></div></div><div class="smallframe-c"></div></a>
				</xsl:when>
				<xsl:when test="$lang='zh_tw' or $lang='zh_cn'">
					<a class="linksmallframe"><div class="smallframe-a"></div><div class="smallframe-b"><xsl:value-of select="$realm" /></div><div class="smallframe-icon"><div class="reldiv"><div class="smallframe-realm"/></div></div><div class="smallframe-c"></div></a>
					<a class="linksmallframe"><div class="smallframe-a"></div><div class="smallframe-b"><xsl:value-of select="$battleGroup" /></div><div class="smallframe-icon"><div class="reldiv"><div class="smallframe-battlegroup"/></div></div><div class="smallframe-c"></div></a>
				</xsl:when>
				<xsl:otherwise>
					<a class="linksmallframe" href="javascript:forumLink(&quot;{$realm}&quot;,&quot;{$lang}&quot;)"><div class="smallframe-a"></div><div class="smallframe-b staticTip"><xsl:attribute name="onMouseOver"> setTipText("<xsl:value-of select="$loc/strs/unsorted/str[@id='armory.forum.link.realm']"/>");</xsl:attribute><xsl:value-of select="$realm" /></div><div class="smallframe-icon"><div class="reldiv"><div class="smallframe-realm"/></div></div><div class="smallframe-c"></div></a>
					<a class="linksmallframe" href="javascript:forumLink(&quot;{$battleGroup}&quot;,&quot;{$lang}&quot;)"><div class="smallframe-a"></div><div class="smallframe-b staticTip"><xsl:attribute name="onMouseOver"> setTipText("<xsl:value-of select="$loc/strs/unsorted/str[@id='armory.forum.link.battleGroup']"/>");</xsl:attribute><xsl:value-of select="$battleGroup"/></div><div class="smallframe-icon"><div class="reldiv"><div class="smallframe-battlegroup"/></div></div><div class="smallframe-c"></div></a>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</div>
</div>


</div>
</div><!--/profile-header/-->

<xsl:variable name = "headerText" select="concat('armory.labels.header.', $headerKey)" />
<!--<div class="parch-profile-banner" id="banner" style="margin-top: -7px!important;" >
	<h1><xsl:value-of select="$loc/strs/unsorted/str[@id=$headerText]" /></h1>
</div>-->


<script type="text/javascript">


		pinCharName = "<xsl:value-of select="character/@name" />";
		pinRealmName = "<xsl:value-of select="character/@realm" />";
		pinGuildName = "<xsl:value-of select="character/@guildName" />";
		pinGuildUrl = "<xsl:value-of select="character/@guildUrl" />";
		pinFactionId = "<xsl:value-of select="character/@factionId" />";
		pinRace = "<xsl:value-of select="character/@race" />";
		pinClassName = "<xsl:value-of select="character/@class" />";

		pinTeam2Url = "<xsl:value-of select="character/arenaTeams/arenaTeam[@size = 2]/@url" />";
		pinTeam3Url = "<xsl:value-of select="character/arenaTeams/arenaTeam[@size = 3]/@url" />";
		pinTeam5Url = "<xsl:value-of select="character/arenaTeams/arenaTeam[@size = 5]/@url" />";

</script>

</xsl:template>
</xsl:stylesheet>
