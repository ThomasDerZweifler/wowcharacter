<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"/>

<xsl:include href="includes.xsl"/>
<xsl:include href="character-header.xsl"/>
<xsl:include href="character-utils.xsl"/>
<xsl:include href="mini-search-templates.xsl"/>

<xsl:template match="page/characterInfo">

<xsl:variable name="isPropass">
  <xsl:choose>
    <xsl:when test="character/@tournamentRealm">true</xsl:when>
	<xsl:otherwise>false</xsl:otherwise>
  </xsl:choose>
</xsl:variable>

<xsl:variable name="theCharName" select="character/@name" />

<xsl:variable name="pathPropass" select="/page/characterInfo/character/arenaTeams/arenaTeam[@size=3]" />
<xsl:variable name="pathPropassChar" select="/page/characterInfo/character/arenaTeams/arenaTeam[@size=3]/members/character[@name=$theCharName]" />

<span style="display:none;">start</span><!--needed to fix IE bug that ignores script includes-->

<div id="dataElement">


<xsl:variable name="theClassId" select="character/@classId" />
<xsl:variable name="theRaceId" select="character/@raceId" />
<xsl:variable name="theGenderId" select="character/@genderId" />
<xsl:variable name="theFactionId" select="character/@factionId" />
<xsl:variable name="charUrl" select="character/@charUrl" />
<xsl:variable name="level" select="character/@level" />
<xsl:variable name="lastModified" select="character/@lastModified" />
<xsl:variable name="guildUrl" select="character/@guildUrl" />

<xsl:variable name="recUpgradeUrlPre">
    <xsl:call-template name="search-and-replace">
        <xsl:with-param name="input" select="$charUrl" />
        <xsl:with-param name="search-string" select="'r='" />
        <xsl:with-param name="replace-string" select="'pr='" />
    </xsl:call-template>
</xsl:variable>

<xsl:variable name="recUpgradeUrl">
    <xsl:call-template name="search-and-replace">
        <xsl:with-param name="input" select="$recUpgradeUrlPre" />
        <xsl:with-param name="search-string" select="'n='" />
        <xsl:with-param name="replace-string" select="'pn='" />
    </xsl:call-template>
</xsl:variable>

<script type="text/javascript">

	var theClassId=<xsl:value-of select="$theClassId" />;
	var theRaceId=<xsl:value-of select="$theRaceId" />;
	var theClassName="<xsl:value-of select="character/@class" />";
	var theLevel=<xsl:value-of select="$level" />;
	
	var theRealmName="<xsl:value-of select="character/@realm" />";
	var theCharName="<xsl:value-of select="character/@name" />";
	
	var recUpgradeURL = "<xsl:value-of select="$recUpgradeUrl" />";
	

</script>

<div class="parchment-top">
<xsl:call-template name="character-tabs">
  <xsl:with-param name="thePage" select="'character-sheet'"/>
  <xsl:with-param name="charUrl" select="$charUrl"/>
  <xsl:with-param name="guildUrl" select="$guildUrl"/>
  <xsl:with-param name="charLevel" select="$level"/>
  <xsl:with-param name="isPropass" select="$isPropass"/>
  <xsl:with-param name="pathPropass" select="$pathPropass"/>
</xsl:call-template>
  <div class="parchment-content">
<div class="mini-search-start-state" id="results-side-switch">
 <div class="list">
  <div class="player-side">
		<xsl:if test="not(characterTab)">
			<xsl:attribute name="class">player-side notab</xsl:attribute>
		</xsl:if>
			<div class="info-pane">
<xsl:choose>

<xsl:when test="character"><!--does a char exist-->

 <div class="profile-wrapper">
<!-- begin character header -->
  <div class="profile">
   <div>
	 <xsl:attribute name="class">
	  <xsl:choose>
	   <xsl:when test="$isPropass='true'">faction-propass</xsl:when>
	   <xsl:when test="character/@factionId=0">faction-alliance</xsl:when>
	   <xsl:otherwise>faction-horde</xsl:otherwise>
	  </xsl:choose>
	 </xsl:attribute>

	  <div class="profile-left">
	   <div class="profile-right">
		<div class="profile-content">
	<xsl:call-template name="character-header">
	  <xsl:with-param name="headerKey" select="'profile'" />
	  <xsl:with-param name="genderId" select="$theGenderId" />
	  <xsl:with-param name="raceId" select="$theRaceId" />
	  <xsl:with-param name="classId" select="$theClassId" />
	  <xsl:with-param name="realm" select="character/@realm" />
	  <xsl:with-param name="lang" select="$lang" />
	  <xsl:with-param name="battleGroup" select="character/@battleGroup" />
	  <xsl:with-param name="level" select="$level" />
      <xsl:with-param name="points" select="character/@points" />
	  <xsl:with-param name="title" select="character/@title" />
	  <xsl:with-param name="guildName" select="character/@guildName" />
	  <xsl:with-param name="guildUrl" select="$guildUrl" />
	  <xsl:with-param name="race" select="character/@race" />
	  <xsl:with-param name="class" select="character/@class" />
	  <xsl:with-param name="name" select="character/@name" />
	  <xsl:with-param name="isPropass" select="$isPropass" />
	  <xsl:with-param name="pathPropass" select="$pathPropass"/>
	</xsl:call-template>


	<xsl:choose>
	<xsl:when test="character/@level &gt;= 10"> <!--is the character above level 10-->

	  <xsl:choose>
	  <xsl:when test="characterTab"><!--is the info available-->
<xsl:variable name="theSecondBar" select="characterTab/characterBars/secondBar/@type" />
<xsl:variable name="resistArcaneValue" select="characterTab/resistances/arcane/@value" />
<xsl:variable name="resistFireValue" select="characterTab/resistances/fire/@value" />
<xsl:variable name="resistNatureValue" select="characterTab/resistances/nature/@value" />
<xsl:variable name="resistShadowValue" select="characterTab/resistances/shadow/@value" />
<xsl:variable name="resistFrostValue" select="characterTab/resistances/frost/@value" />

<script type="text/javascript">
	<!-- when page loads -->
	$(document).ready(function() {
		
		//set up character sheeet
		setCharSheetIcons();
		setCharSheetLinks();			
		setCharSheetUpgradeMenu();		
	});

</script>
<script type="text/javascript">
	var charUrl = "<xsl:value-of select="/page/characterInfo/character/@charUrl"/>";
	var bookmarkMaxedToolTip = "<xsl:value-of select="$loc/strs/common/str[@id='user.bookmark.maxedtooltip']"/>";
	
</script>

<script type="text/javascript" src="/js/character/functions.js"></script>
	<xsl:variable name="characterUrl" select="/page/characterInfo/character/@charUrl"/>
	<xsl:variable name="profile" select="document(concat('/login-status.xml?',$characterUrl))/page/loginStatus" />
    
    <xsl:choose>
    <xsl:when test="document('/login-status.xml')/page/loginStatus/@username != ''">    
        <xsl:choose>   
            <xsl:when test="/page/characterInfo/bookmark/@bookmarked">
                <div class="bmcEnabled"></div>
            </xsl:when>
            <xsl:when test="/page/characterInfo/bookmark/@count &gt;= /page/characterInfo/bookmark/@max">
                <div class="bmcMaxed staticTip" onmouseover="setTipText(bookmarkMaxedToolTip)"></div>
            </xsl:when>
            <xsl:otherwise>
                <a class="bmcLink" href="javascript:ajaxBookmarkChar()"><span><xsl:value-of select="$loc/strs/common/str[@id='user.bookmark.character']"/></span><em></em></a>
            </xsl:otherwise>
        </xsl:choose>    
    </xsl:when>
    <xsl:otherwise>
    	<a id="loginLinkRedirect2" class="bmcLink"><span><xsl:value-of select="$loc/strs/common/str[@id='user.bookmark.logintobookmark']"/></span><em></em></a>    
    </xsl:otherwise>    
    </xsl:choose>
<script type="text/javascript">

function strengthObject() {
	this.base="<xsl:value-of select="characterTab/baseStats/strength/@base" />";
	this.effective="<xsl:value-of select="characterTab/baseStats/strength/@effective" />";
	this.block="<xsl:value-of select="characterTab/baseStats/strength/@block" />";
	this.attack="<xsl:value-of select="characterTab/baseStats/strength/@attack" />";

	this.diff=this.effective - this.base;
}

function agilityObject() {
	this.base="<xsl:value-of select="characterTab/baseStats/agility/@base" />";
	this.effective="<xsl:value-of select="characterTab/baseStats/agility/@effective" />";
	this.critHitPercent="<xsl:value-of select="characterTab/baseStats/agility/@critHitPercent" />";
	this.attack="<xsl:value-of select="characterTab/baseStats/agility/@attack" />";
	this.armor="<xsl:value-of select="characterTab/baseStats/agility/@armor" />";

	this.diff=this.effective - this.base;
}

function staminaObject(base, effective, health, petBonus) {
	this.base="<xsl:value-of select="characterTab/baseStats/stamina/@base" />";
	this.effective="<xsl:value-of select="characterTab/baseStats/stamina/@effective" />";
	this.health="<xsl:value-of select="characterTab/baseStats/stamina/@health" />";
	this.petBonus="<xsl:value-of select="characterTab/baseStats/stamina/@petBonus" />";

	this.diff=this.effective - this.base;
}

function intellectObject() {
	this.base="<xsl:value-of select="characterTab/baseStats/intellect/@base" />";
	this.effective="<xsl:value-of select="characterTab/baseStats/intellect/@effective" />";
	this.mana="<xsl:value-of select="characterTab/baseStats/intellect/@mana" />";
	this.critHitPercent="<xsl:value-of select="characterTab/baseStats/intellect/@critHitPercent" />";
	this.petBonus="<xsl:value-of select="characterTab/baseStats/intellect/@petBonus" />";

	this.diff=this.effective - this.base;
}

function spiritObject() {
	this.base="<xsl:value-of select="characterTab/baseStats/spirit/@base" />";
	this.effective="<xsl:value-of select="characterTab/baseStats/spirit/@effective" />";
	this.healthRegen="<xsl:value-of select="characterTab/baseStats/spirit/@healthRegen" />";
	this.manaRegen="<xsl:value-of select="characterTab/baseStats/spirit/@manaRegen" />";

	this.diff=this.effective - this.base;
}

function armorObject() {
	this.base="<xsl:value-of select="characterTab/baseStats/armor/@base" />";
	this.effective="<xsl:value-of select="characterTab/baseStats/armor/@effective" />";
	this.reductionPercent="<xsl:value-of select="characterTab/baseStats/armor/@percent" />";
	this.petBonus="<xsl:value-of select="characterTab/baseStats/armor/@petBonus" />";

	this.diff=this.effective - this.base;
}

function resistancesObject() {
	this.arcane=new resistArcaneObject("<xsl:value-of select="$resistArcaneValue" />", "<xsl:value-of select="characterTab/resistances/arcane/@petBonus" />");
	this.nature=new resistNatureObject("<xsl:value-of select="$resistNatureValue" />", "<xsl:value-of select="characterTab/resistances/nature/@petBonus" />");
	this.fire=new resistFireObject("<xsl:value-of select="$resistFireValue" />", "<xsl:value-of select="characterTab/resistances/fire/@petBonus" />");
	this.frost=new resistFrostObject("<xsl:value-of select="$resistFrostValue" />", "<xsl:value-of select="characterTab/resistances/frost/@petBonus" />");
	this.shadow=new resistShadowObject("<xsl:value-of select="$resistShadowValue" />", "<xsl:value-of select="characterTab/resistances/shadow/@petBonus" />");
}

function meleeMainHandWeaponSkillObject() {
	this.value="<xsl:value-of select="characterTab/melee/expertise/@value" />";
	this.rating="<xsl:value-of select="characterTab/melee/expertise/@rating" />";
	this.additional="<xsl:value-of select="characterTab/melee/expertise/@additional" />";
	this.percent="<xsl:value-of select="characterTab/melee/expertise/@percent" />";
}

function meleeOffHandWeaponSkillObject() {
	this.value="<xsl:value-of select="characterTab/melee/offHandWeaponSkill/@value" />";
	this.rating="<xsl:value-of select="characterTab/melee/offHandWeaponSkill/@rating" />";
}


function meleeMainHandDamageObject() {
	this.speed="<xsl:value-of select="characterTab/melee/mainHandDamage/@speed" />";
	this.min="<xsl:value-of select="characterTab/melee/mainHandDamage/@min" />";
	this.max="<xsl:value-of select="characterTab/melee/mainHandDamage/@max" />";
	this.percent="<xsl:value-of select="characterTab/melee/mainHandDamage/@percent" />";
	this.dps="<xsl:value-of select="characterTab/melee/mainHandDamage/@dps" />";

	if (this.percent &gt; 0)		this.effectiveColor="class='mod'";
	else if (this.percent &lt; 0)	this.effectiveColor="class='moddown'";
}

function meleeOffHandDamageObject() {
	this.speed="<xsl:value-of select="characterTab/melee/offHandDamage/@speed" />";
	this.min="<xsl:value-of select="characterTab/melee/offHandDamage/@min" />";
	this.max="<xsl:value-of select="characterTab/melee/offHandDamage/@max" />";
	this.percent="<xsl:value-of select="characterTab/melee/offHandDamage/@percent" />";
	this.dps="<xsl:value-of select="characterTab/melee/offHandDamage/@dps" />";
}


function meleeMainHandSpeedObject() {
	this.value="<xsl:value-of select="characterTab/melee/mainHandSpeed/@value" />";
	this.hasteRating="<xsl:value-of select="characterTab/melee/mainHandSpeed/@hasteRating" />";
	this.hastePercent="<xsl:value-of select="characterTab/melee/mainHandSpeed/@hastePercent" />";
}

function meleeOffHandSpeedObject() {
	this.value="<xsl:value-of select="characterTab/melee/offHandSpeed/@value" />";
	this.hasteRating="<xsl:value-of select="characterTab/melee/offHandSpeed/@hasteRating" />";
	this.hastePercent="<xsl:value-of select="characterTab/melee/offHandSpeed/@hastePercent" />";
}

function meleePowerObject() {
	this.base="<xsl:value-of select="characterTab/melee/power/@base" />";
	this.effective="<xsl:value-of select="characterTab/melee/power/@effective" />";
	this.increasedDps="<xsl:value-of select="characterTab/melee/power/@increasedDps" />";

	this.diff=this.effective - this.base;
}

function meleeHitRatingObject() {
	this.value="<xsl:value-of select="characterTab/melee/hitRating/@value" />";
	this.increasedHitPercent="<xsl:value-of select="characterTab/melee/hitRating/@increasedHitPercent" />";
	this.armorPenetration="<xsl:value-of select="characterTab/melee/hitRating/@penetration" />";	
	this.reducedArmorPercent="<xsl:value-of select="characterTab/melee/hitRating/@reducedArmorPercent" />";
}

function meleeCritChanceObject() {
	this.percent="<xsl:value-of select="characterTab/melee/critChance/@percent" />";
	this.rating="<xsl:value-of select="characterTab/melee/critChance/@rating" />";
	this.plusPercent="<xsl:value-of select="characterTab/melee/critChance/@plusPercent" />";
}

function rangedWeaponSkillObject() {
	this.value=<xsl:value-of select="characterTab/ranged/weaponSkill/@value" />;
	this.rating=<xsl:value-of select="characterTab/ranged/weaponSkill/@rating" />;
}

function rangedDamageObject() {
	this.speed=<xsl:value-of select="characterTab/ranged/damage/@speed" />;
	this.min=<xsl:value-of select="characterTab/ranged/damage/@min" />;
	this.max=<xsl:value-of select="characterTab/ranged/damage/@max" />;
	this.dps=<xsl:value-of select="characterTab/ranged/damage/@dps" />;
	this.percent=<xsl:value-of select="characterTab/ranged/damage/@percent" />;

	if (this.percent &gt; 0)		this.effectiveColor="class='mod'";
	else if (this.percent &lt; 0)	this.effectiveColor="class='moddown'";

}

function rangedSpeedObject() {
	this.value=<xsl:value-of select="characterTab/ranged/speed/@value" />;
	this.hasteRating=<xsl:value-of select="characterTab/ranged/speed/@hasteRating" />;
	this.hastePercent=<xsl:value-of select="characterTab/ranged/speed/@hastePercent" />;
}

function rangedPowerObject() {
	this.base=<xsl:value-of select="characterTab/ranged/power/@base" />;
	this.effective=<xsl:value-of select="characterTab/ranged/power/@effective" />;
	this.increasedDps=<xsl:value-of select="characterTab/ranged/power/@increasedDps" />;
	this.petAttack=<xsl:value-of select="characterTab/ranged/power/@petAttack" />;
	this.petSpell=<xsl:value-of select="characterTab/ranged/power/@petSpell" />;

	this.diff=this.effective - this.base;
}

function rangedHitRatingObject() {
	this.value="<xsl:value-of select="characterTab/ranged/hitRating/@value" />";
	this.increasedHitPercent="<xsl:value-of select="characterTab/ranged/hitRating/@increasedHitPercent" />";
	this.armorPenetration="<xsl:value-of select="characterTab/ranged/hitRating/@penetration" />";
	this.reducedArmorPercent="<xsl:value-of select="characterTab/ranged/hitRating/@reducedArmorPercent" />";
}

function rangedCritChanceObject() {
	this.percent=<xsl:value-of select="characterTab/ranged/critChance/@percent" />;
	this.rating=<xsl:value-of select="characterTab/ranged/critChance/@rating" />;
	this.plusPercent=<xsl:value-of select="characterTab/ranged/critChance/@plusPercent" />;
}

function spellBonusDamageObject() {
	this.holy=<xsl:value-of select="characterTab/spell/bonusDamage/holy/@value" />;
	this.arcane=<xsl:value-of select="characterTab/spell/bonusDamage/arcane/@value" />;
	this.fire=<xsl:value-of select="characterTab/spell/bonusDamage/fire/@value" />;
	this.nature=<xsl:value-of select="characterTab/spell/bonusDamage/nature/@value" />;
	this.frost=<xsl:value-of select="characterTab/spell/bonusDamage/frost/@value" />;
	this.shadow=<xsl:value-of select="characterTab/spell/bonusDamage/shadow/@value" />;
	this.petBonusAttack=<xsl:value-of select="characterTab/spell/bonusDamage/petBonus/@attack" />;
	this.petBonusDamage=<xsl:value-of select="characterTab/spell/bonusDamage/petBonus/@damage" />;
	this.petBonusFromType="<xsl:value-of select="characterTab/spell/bonusDamage/petBonus/@fromType" />";

	this.value=this.holy;
	
	if (this.value &gt; this.arcane)	this.value=this.arcane;
	if (this.value &gt; this.fire)		this.value=this.fire;
	if (this.value &gt; this.nature)	this.value=this.nature;
	if (this.value &gt; this.frost)		this.value=this.frost;
	if (this.value &gt; this.shadow)	this.value=this.shadow;
}

function spellBonusHealingObject() {
	this.value=<xsl:value-of select="characterTab/spell/bonusHealing/@value" />;
}

function spellHasteRatingObject(){
	this.value=<xsl:value-of select="characterTab/spell/hasteRating/@hasteRating" />;
	this.percent=<xsl:value-of select="characterTab/spell/hasteRating/@hastePercent" />;
}

function spellHitRatingObject() {
	this.value=<xsl:value-of select="characterTab/spell/hitRating/@value" />;
	this.increasedHitPercent=<xsl:value-of select="characterTab/spell/hitRating/@increasedHitPercent" />;
	this.spellPenetration= <xsl:value-of select="characterTab/spell/penetration/@value" />;	
}

function spellCritChanceObject() {
	this.rating=<xsl:value-of select="characterTab/spell/critChance/@rating" />;
	this.holy=<xsl:value-of select="characterTab/spell/critChance/holy/@percent" />;
	this.arcane=<xsl:value-of select="characterTab/spell/critChance/arcane/@percent" />;
	this.fire=<xsl:value-of select="characterTab/spell/critChance/fire/@percent" />;
	this.nature=<xsl:value-of select="characterTab/spell/critChance/nature/@percent" />;
	this.frost=<xsl:value-of select="characterTab/spell/critChance/frost/@percent" />;
	this.shadow=<xsl:value-of select="characterTab/spell/critChance/shadow/@percent" />;

	this.percent=this.holy;
	
	if (this.percent &gt; this.arcane)	this.percent=this.arcane;
	if (this.percent &gt; this.fire)	this.percent=this.fire;
	if (this.percent &gt; this.nature)	this.percent=this.nature;
	if (this.percent &gt; this.frost)	this.percent=this.frost;
	if (this.percent &gt; this.shadow)	this.percent=this.shadow;
}

function spellPenetrationObject() {
	this.value=<xsl:value-of select="characterTab/spell/penetration/@value" />;
}

function spellManaRegenObject() {
	this.casting=<xsl:value-of select="characterTab/spell/manaRegen/@casting" />;
	this.notCasting=<xsl:value-of select="characterTab/spell/manaRegen/@notCasting" />;
}

function defensesArmorObject() {
	this.base=<xsl:value-of select="characterTab/defenses/armor/@base" />;
	this.effective=<xsl:value-of select="characterTab/defenses/armor/@effective" />;
	this.percent=<xsl:value-of select="characterTab/defenses/armor/@percent" />;
	this.petBonus=<xsl:value-of select="characterTab/defenses/armor/@petBonus" />;

	this.diff=this.effective - this.base;
}

function defensesDefenseObject() {
	this.rating=<xsl:value-of select="characterTab/defenses/defense/@rating" />;
	this.plusDefense=<xsl:value-of select="characterTab/defenses/defense/@plusDefense" />;
	this.increasePercent=<xsl:value-of select="characterTab/defenses/defense/@increasePercent" />;
	this.decreasePercent=<xsl:value-of select="characterTab/defenses/defense/@decreasePercent" />;
	this.value=<xsl:value-of select="characterTab/defenses/defense/@value" /> + this.plusDefense;
}

function defensesDodgeObject() {
	this.percent=<xsl:value-of select="characterTab/defenses/dodge/@percent" />;
	this.rating=<xsl:value-of select="characterTab/defenses/dodge/@rating" />;
	this.increasePercent=<xsl:value-of select="characterTab/defenses/dodge/@increasePercent" />;
}

function defensesParryObject() {
	this.percent=<xsl:value-of select="characterTab/defenses/parry/@percent" />;
	this.rating=<xsl:value-of select="characterTab/defenses/parry/@rating" />;
	this.increasePercent=<xsl:value-of select="characterTab/defenses/parry/@increasePercent" />;
}

function defensesBlockObject() {
	this.percent=<xsl:value-of select="characterTab/defenses/block/@percent" />;
	this.rating=<xsl:value-of select="characterTab/defenses/block/@rating" />;
	this.increasePercent=<xsl:value-of select="characterTab/defenses/block/@increasePercent" />;
}

function defensesResilienceObject() {
	this.value=<xsl:value-of select="characterTab/defenses/resilience/@value" />;
	this.hitPercent=<xsl:value-of select="characterTab/defenses/resilience/@hitPercent" />;
	this.damagePercent=<xsl:value-of select="characterTab/defenses/resilience/@damagePercent" />;
}


var theCharacter=new characterObject();

var theCharUrl="<xsl:value-of select="$charUrl" />";

</script>
<script type="text/javascript" src="/js/{$lang}/character-sheet.js"></script>

<script type="text/javascript">

  var itemsArray=new Array;

  <xsl:for-each select="characterTab/items/item">
    itemsArray[<xsl:value-of select="@slot" />] =[<xsl:value-of select="@id" />  , "<xsl:value-of select="@icon" />", "<xsl:value-of select="@durability" />", "<xsl:value-of select="@maxDurability" />"];
  </xsl:for-each>

</script>

<xsl:variable name="pathItem" select="/page/characterInfo/characterTab/items"/>
<xsl:variable name="pathHead" select="$pathItem/item[@slot=0]"/>
<xsl:variable name="pathNeck" select="$pathItem/item[@slot=1]"/>
<xsl:variable name="pathShoulders" select="$pathItem/item[@slot=2]"/>
<xsl:variable name="pathBack" select="$pathItem/item[@slot=14]"/>
<xsl:variable name="pathChest" select="$pathItem/item[@slot=4]"/>
<xsl:variable name="pathShirt" select="$pathItem/item[@slot=3]"/>
<xsl:variable name="pathTabard" select="$pathItem/item[@slot=18]"/>
<xsl:variable name="pathWrist" select="$pathItem/item[@slot=8]"/>

<xsl:variable name="pathHands" select="$pathItem/item[@slot=9]"/>
<xsl:variable name="pathWaist" select="$pathItem/item[@slot=5]"/>
<xsl:variable name="pathLegs" select="$pathItem/item[@slot=6]"/>
<xsl:variable name="pathFeet" select="$pathItem/item[@slot=7]"/>
<xsl:variable name="pathRingOne" select="$pathItem/item[@slot=10]"/>
<xsl:variable name="pathRingTwo" select="$pathItem/item[@slot=11]"/>
<xsl:variable name="pathTrinketOne" select="$pathItem/item[@slot=12]"/>
<xsl:variable name="pathTrinketTwo" select="$pathItem/item[@slot=13]"/>

<xsl:variable name="pathMainHand" select="$pathItem/item[@slot=15]"/>
<xsl:variable name="pathOffHand" select="$pathItem/item[@slot=16]"/>
<xsl:variable name="pathRanged" select="$pathItem/item[@slot=17]"/>

<xsl:variable name="textFindUpgrade" select="$loc/strs/characterSheet/str[@id='armory.character-sheet.findupgrade']"/>


<div class="profile-master" style="height: 500px;">
 <div class="stack1"><img src="/images/pixel.gif" class="ieimg" width="1" height="1" />
  <div class="items-left">
   <ul>
    <li id="_slot0"><em id="{$pathHead/@icon}" class="charSheetImg"></em><a class="staticTip itemToolTip thisTip" id="{$pathHead/@id}"></a></li>
    <li id="_slot1"><em id="{$pathNeck/@icon}" class="charSheetImg"></em><a class="staticTip itemToolTip thisTip" id="{$pathNeck/@id}"></a></li>
    <li id="_slot2"><em id="{$pathShoulders/@icon}" class="charSheetImg"></em><a class="staticTip itemToolTip thisTip" id="{$pathShoulders/@id}"></a></li>
    <li id="_slot14"><em id="{$pathBack/@icon}" class="charSheetImg"></em><a class="staticTip itemToolTip thisTip" id="{$pathBack/@id}"></a></li>
    <li id="_slot4"><em id="{$pathChest/@icon}" class="charSheetImg"></em><a class="staticTip itemToolTip thisTip" id="{$pathChest/@id}"></a></li>
    <li id="_slot3" class="noUpgrade"><em id="{$pathShirt/@icon}" class="charSheetImg"></em><a class="staticTip itemToolTip thisTip" id="{$pathShirt/@id}"></a></li>
    <li id="_slot18" class="noUpgrade"><em id="{$pathTabard/@icon}" class="charSheetImg"></em><a class="staticTip itemToolTip thisTip" id="{$pathTabard/@id}"></a></li>
    <li id="_slot8"><em id="{$pathWrist/@icon}" class="charSheetImg"></em><a class="staticTip itemToolTip thisTip" id="{$pathWrist/@id}"></a></li>
   </ul>
  </div><!--/items-left/-->
  <div class="items-right">
   <ul>
    <li id="_slot9"><em id="{$pathHands/@icon}" class="charSheetImg"></em><a class="staticTip itemToolTip thisTip" id="{$pathHands/@id}"></a></li>
    <li id="_slot5"><em id="{$pathWaist/@icon}" class="charSheetImg"></em><a class="staticTip itemToolTip thisTip" id="{$pathWaist/@id}"></a></li>
    <li id="_slot6"><em id="{$pathLegs/@icon}" class="charSheetImg"></em><a class="staticTip itemToolTip thisTip" id="{$pathLegs/@id}"></a></li>
    <li id="_slot7"><em id="{$pathFeet/@icon}" class="charSheetImg"></em><a class="staticTip itemToolTip thisTip" id="{$pathFeet/@id}"></a></li>
    <li id="_slot10"><em id="{$pathRingOne/@icon}" class="charSheetImg"></em><a class="staticTip itemToolTip thisTip" id="{$pathRingOne/@id}"></a></li>
    <li id="_slot11"><em id="{$pathRingTwo/@icon}" class="charSheetImg"></em><a class="staticTip itemToolTip thisTip" id="{$pathRingTwo/@id}"></a></li>
    <li id="_slot12"><em id="{$pathTrinketOne/@icon}" class="charSheetImg"></em><a class="staticTip itemToolTip thisTip" id="{$pathTrinketOne/@id}"></a></li>
    <li id="_slot13"><em id="{$pathTrinketTwo/@icon}" class="charSheetImg"></em><a class="staticTip itemToolTip thisTip" id="{$pathTrinketTwo/@id}"></a></li>
   </ul>
  </div><!--/items-right/-->
  <div class="buffs">
   <ul>
     <xsl:for-each select="characterTab/buffs/spell">
	   <xsl:variable name="buffIcon">
	     <xsl:choose>
	       <xsl:when test="@icon != 'Temp'"><xsl:value-of select="@icon"/></xsl:when>
		   <xsl:otherwise>inv_misc_questionmark</xsl:otherwise>
	     </xsl:choose>
	   </xsl:variable>
		  <script type="text/javascript">
			buffArray[<xsl:value-of select="position()" />]="&lt;span class='tooltipContentSpecial tooltipTitle'&gt;<xsl:value-of select="@name" />&lt;/span&gt;\
<xsl:call-template name="search-and-replace">
  <xsl:with-param name="input" select="@effect" />
  <xsl:with-param name="search-string" select="'&#10;&#10;'" />
  <xsl:with-param name="replace-string" select="'&lt;br /&gt;'" />
</xsl:call-template>";
		  </script>
		   <li><img src="/wow-icons/_images/21x21/{$buffIcon}.png" height="21" width="21" onMouseOver="setTipText(buffArray[{position()}]);" class="ci staticTip" /></li>
	 </xsl:for-each>
   </ul>
  </div>
  <div class="debuffs">
   <ul>
     <xsl:for-each select="characterTab/debuffs/spell">
	   <xsl:variable name="debuffIcon">
	     <xsl:choose>
	       <xsl:when test="@icon != 'Temp'"><xsl:value-of select="@icon"/></xsl:when>
		   <xsl:otherwise>inv_misc_questionmark</xsl:otherwise>
	     </xsl:choose>
	   </xsl:variable>
			<script type="text/javascript">
				debuffArray[<xsl:value-of select="position()" />]="&lt;span class='tooltipContentSpecial tooltipTitle'&gt;<xsl:value-of select="@name" />&lt;/span&gt;<xsl:value-of select="@effect" />";
			</script>
			<li><img src="/wow-icons/_images/21x21/{$debuffIcon}.png" class="ci staticTip" onMouseOver="setTipText(debuffArray[{position()}]);" /></li>
	 </xsl:for-each>
   </ul>
  </div>
  <div class="spec"><img src="/images/pixel.gif" height="1" width="1" class="ieimg" /> 
   <em class="ptl"><xsl:comment/></em><em class="ptr"><xsl:comment/></em><em class="pbl"><xsl:comment/></em><em class="pbr"><xsl:comment/></em>
   <h4><xsl:value-of select="$loc/strs/characterSheet/str[@id='armory.character.talent-specialization']"/></h4>
   <div class="spec-wrapper"><div style="position:absolute; left:15px;"><img id="talentSpecImage" /></div>
   <h4><a href="character-talents.xml?{$charUrl}"><div id="replaceTalentSpecText"></div></a></h4><span><xsl:value-of select="characterTab/talentSpec/@treeOne" /> / <xsl:value-of select="characterTab/talentSpec/@treeTwo" /> / <xsl:value-of select="characterTab/talentSpec/@treeThree" /></span></div>

<span style="display:none;">start</span><!--needed to fix IE bug that ignores script includes-->
<script type="text/javascript">

	var talentsTreeArray=new Array;
	talentsTreeArray[0]=[1, <xsl:value-of select="characterTab/talentSpec/@treeOne" />, "<xsl:value-of select="$loc/strs/talents/str[@id=concat('armory.class', $theClassId, '.talents.tree.one')]"/>"];
	talentsTreeArray[1]=[2, <xsl:value-of select="characterTab/talentSpec/@treeTwo" />, "<xsl:value-of select="$loc/strs/talents/str[@id=concat('armory.class', $theClassId, '.talents.tree.two')]"/>"];
	talentsTreeArray[2]=[3, <xsl:value-of select="characterTab/talentSpec/@treeThree" />, "<xsl:value-of select="$loc/strs/talents/str[@id=concat('armory.class', $theClassId, '.talents.tree.three')]"/>"];
	
	var theUpgradeTxt = "<xsl:value-of select="$textFindUpgrade" />";
</script>

  </div>
  <div class="resists">
   <em class="ptl"><xsl:comment/></em><em class="ptr"><xsl:comment/></em><em class="pbl"><xsl:comment/></em><em class="pbr"><xsl:comment/></em>
   <h4><xsl:value-of select="$loc/strs/characterSheet/str[@id='armory.character.resistances']"/></h4>
   <ul>
    <li onMouseOver="setTipText(theText.resistances.arcane.tooltip);" class="arcane staticTip"><b><xsl:value-of select="characterTab/resistances/arcane/@value" /></b>
	<span id="spanResistArcane"><xsl:value-of select="characterTab/resistances/arcane/@value" /></span><h4><a><xsl:value-of select="$loc/strs/spellType/str[@id='armory.spell-type.arcane']"/></a></h4></li>

    <li onMouseOver="setTipText(theText.resistances.fire.tooltip);" class="fire staticTip"><b><xsl:value-of select="characterTab/resistances/fire/@value" /></b>
	<span id="spanResistFire"><xsl:value-of select="characterTab/resistances/fire/@value" /></span><h4><a><xsl:value-of select="$loc/strs/spellType/str[@id='armory.spell-type.fire']"/></a></h4></li>

    <li onMouseOver="setTipText(theText.resistances.nature.tooltip);" class="nature staticTip"><b><xsl:value-of select="characterTab/resistances/nature/@value" /></b>
	<span id="spanResistNature"><xsl:value-of select="characterTab/resistances/nature/@value" /></span><h4><a><xsl:value-of select="$loc/strs/spellType/str[@id='armory.spell-type.nature']"/></a></h4></li>

    <li onMouseOver="setTipText(theText.resistances.frost.tooltip);" class="frost staticTip"><b><xsl:value-of select="characterTab/resistances/frost/@value" /></b>
	<span id="spanResistFrost"><xsl:value-of select="characterTab/resistances/frost/@value" /></span><h4><a><xsl:value-of select="$loc/strs/spellType/str[@id='armory.spell-type.frost']"/></a></h4></li>

    <li onMouseOver="setTipText(theText.resistances.shadow.tooltip);" class="shadow staticTip"><b><xsl:value-of select="characterTab/resistances/shadow/@value" /></b>
	<span id="spanResistShadow"><xsl:value-of select="characterTab/resistances/shadow/@value" /></span><h4><a><xsl:value-of select="$loc/strs/spellType/str[@id='armory.spell-type.shadow']"/></a></h4></li>

   </ul>
  </div>
  <div class="profs">
   <em class="ptl"><xsl:comment/></em><em class="ptr"><xsl:comment/></em><em class="pbl"><xsl:comment/></em><em class="pbr"><xsl:comment/></em>


   <xsl:choose>
   <xsl:when test="$isPropass='true'"><h4><xsl:value-of select="$loc/strs/propass/str[@id='pvpinfo']"/></h4>

   <div class="tooltipContentSpecial" style="padding-left: 15px; padding-top: 5px;">
   <span style="font-size: 12px; font-weight: bold; "><xsl:value-of select="$loc/strs/propass/str[@id='personalrating']"/>&#160;</span><span style="color:#FFFFFF; font-size: 14px;"><xsl:choose><xsl:when test="$pathPropassChar"><xsl:value-of select="$pathPropassChar/@contribution" /></xsl:when><xsl:otherwise><span style="font-size: 11px;">N/A</span></xsl:otherwise></xsl:choose></span><p />
   <span style="font-size: 10px;"><xsl:value-of select="$loc/strs/propass/str[@id='winrecord']"/>&#160;</span><xsl:choose><xsl:when test="$pathPropassChar"><span style="color:#FFFFFF; "><span style="color:#00CC00"><xsl:value-of select="$pathPropassChar/@seasonGamesWon" /></span> - <span style="color:#FF0000 "><xsl:value-of select="$pathPropassChar/@seasonGamesPlayed - $pathPropassChar/@seasonGamesWon" /></span></span></xsl:when><xsl:otherwise><span style="color:#FFFFFF">N/A</span></xsl:otherwise></xsl:choose> <br />
   <span style="font-size: 10px;"><xsl:value-of select="$loc/strs/propass/str[@id='winpercentage']"/>&#160;</span><span style="color:#FFFFFF; "><xsl:choose>
    <xsl:when test="not ($pathPropassChar)"><span style="color:#FFFFFF">N/A</span></xsl:when>
   <xsl:when test="@seasonGamesPlayed=0">0&#37;</xsl:when>
   <xsl:otherwise><xsl:value-of select="round($pathPropassChar/@seasonGamesWon div $pathPropassChar/@seasonGamesPlayed * 100)" />&#37;</xsl:otherwise>
   </xsl:choose></span>
   </div>

   </xsl:when>
   <xsl:otherwise>
   <h4><xsl:value-of select="$loc/strs/characterSheet/str[@id='armory.character.professions-primary']"/></h4>

   <xsl:for-each select="characterTab/professions/skill">

   <xsl:variable name="profValue" select="@value" />
   <xsl:variable name="profMax" select="@max" />

   <div class="prof1">
   <div class="profImage"><img src="/images/icons/professions/{@key}-sm.gif" /></div>
   	<!--<xsl:attribute name="style">
	  background: url('/images/icons/professions/<xsl:value-of select="@key" />-sm.png') no-repeat 15px 17px;
	</xsl:attribute>-->

    <h4><xsl:value-of select="@name" /></h4>
	<div class="bar-container"><img src="/images/pixel.gif" height="1" width="1" class="ieimg" /> 
	<b>
	<xsl:attribute name="style"> width: <xsl:choose><xsl:when test="$profValue &lt; $profMax"><xsl:value-of select="number(100 * $profValue div $profMax)" /></xsl:when><xsl:otherwise>100</xsl:otherwise></xsl:choose>%</xsl:attribute></b><span><xsl:value-of select="@value" /> / <xsl:value-of select="@max" /></span></div>
   </div>
   </xsl:for-each>

   <xsl:choose>
     <xsl:when test="count(characterTab/professions/skill)=0">

   <div class="prof1">
   <div class="profImage"><img src="/images/icons/professions/none-sm.gif" /></div>
    <h4><a><xsl:value-of select="$loc/strs/common/str[@id='armory.none']"/></a></h4>
	<div class="bar-container"><img src="/images/pixel.gif" height="1" width="1" class="ieimg" /> 
	<b>
	<xsl:attribute name="style"> width: 0%</xsl:attribute></b><span><xsl:value-of select="$loc/strs/general/str[@id='armory.labels.na']"/></span></div>
   </div>

	 </xsl:when>
   </xsl:choose>

   <xsl:choose>
     <xsl:when test="count(characterTab/professions/skill)=1 or count(characterTab/professions/skill)=0">
   <div class="prof1">
   <div class="profImage"><img src="/images/icons/professions/none-sm.gif" /></div>
    <h4><a><xsl:value-of select="$loc/strs/common/str[@id='armory.none']"/></a></h4>
	<div class="bar-container"><img src="/images/pixel.gif" height="1" width="1" class="ieimg" /> 
	<b>
	<xsl:attribute name="style"> width: 0%</xsl:attribute></b><span><xsl:value-of select="$loc/strs/general/str[@id='armory.labels.na']"/></span></div>
   </div>
	 </xsl:when>
   </xsl:choose>
   </xsl:otherwise>
   </xsl:choose>

  </div>
</div><!--/stack1/-->
<div class="stack2">
  <em class="ptl"><xsl:comment/></em><em class="ptr"><xsl:comment/></em><em class="pbl"><xsl:comment/></em><em class="pbr"><xsl:comment/></em>
   <div class="health-stat">
    <h4><xsl:value-of select="$loc/strs/character/str[@id='character.health']"/></h4><p><span><xsl:value-of select="characterTab/characterBars/health/@effective" /></span></p>
   </div>
   <div>
   <xsl:attribute name="class">
     <xsl:choose>
	   <xsl:when test="$theSecondBar='r'">rage-stat</xsl:when>
	   <xsl:when test="$theSecondBar='e'">energy-stat</xsl:when>
       <xsl:when test="$theSecondBar='p'">runic-stat</xsl:when>
	   <xsl:otherwise>mana-stat</xsl:otherwise>
	 </xsl:choose>
   </xsl:attribute>

    <h4>

     <xsl:choose>
	   <xsl:when test="$theSecondBar='r'">
	     <xsl:value-of select="$loc/strs/character/str[@id='character.rage']"/>
	   </xsl:when>
	   <xsl:when test="$theSecondBar='e'">
	     <xsl:value-of select="$loc/strs/character/str[@id='character.energy']"/>
	   </xsl:when>
       <xsl:when test="$theSecondBar='p'">
	     <xsl:value-of select="$loc/strs/character/str[@id='character.runicpower']"/>
	   </xsl:when>
	   <xsl:otherwise>
	     <xsl:value-of select="$loc/strs/character/str[@id='character.mana']"/>
	   </xsl:otherwise>
	 </xsl:choose>
	</h4><p><span><xsl:value-of select="characterTab/characterBars/secondBar/@effective" /></span></p>
   </div>
</div><!--/stack2/-->

<div class="stack3">

<xsl:call-template name="dropdownMenu">
<xsl:with-param name="hiddenId" select="'Left'"/>
<xsl:with-param name="defaultValue" select="$loc/strs/characterSheet/str[@id='armory.character-sheet.base-stats.display']" />
  <xsl:with-param name="divClass" select="'dropdown1'" />
<xsl:with-param name="anchorClass" select="'profile-stats'" />
</xsl:call-template>
  <div class="drop-stats" style="display: none; z-index: 99999;" id="dropdownHiddenLeft" onMouseOver="javascript: varOverLeft=1;" onMouseOut="javascript: varOverLeft=0;">
  <div class="tooltip">
  <table><tr><td class="tl" /><td class="t" /><td class="tr" /></tr><tr><td class="l" /><td class="bg">
   <ul>
	<li><a href="#" onClick="changeStats('Left', replaceStringBaseStats, 'BaseStats', baseStatsDisplay); return false;"><xsl:value-of select="$loc/strs/characterSheet/str[@id='armory.character-sheet.base-stats.display']"/><img id="checkLeftBaseStats" src="/images/icons/icon-check.gif" style="visibility: visible;" class="checkmark" /></a></li>
	<li><a href="#" onClick="changeStats('Left', replaceStringMelee, 'Melee', meleeDisplay); return false;"><xsl:value-of select="$loc/strs/characterSheet/str[@id='armory.character-sheet.melee.display']"/><img id="checkLeftMelee" src="/images/icons/icon-check.gif" style="visibility: hidden;" class="checkmark" /></a></li>
	<li><a href="#" onClick="changeStats('Left', replaceStringRanged, 'Ranged', rangedDisplay); return false;"><xsl:value-of select="$loc/strs/characterSheet/str[@id='armory.character-sheet.ranged.display']"/><img id="checkLeftRanged" src="/images/icons/icon-check.gif" style="visibility: hidden;" class="checkmark" /></a></li>
	<li><a href="#" onClick="changeStats('Left', replaceStringSpell, 'Spell', spellDisplay); return false;"><xsl:value-of select="$loc/strs/characterSheet/str[@id='armory.character-sheet.spell.display']"/><img id="checkLeftSpell" src="/images/icons/icon-check.gif" style="visibility: hidden;" class="checkmark" /></a></li>
	<li><a href="#" onClick="changeStats('Left', replaceStringDefenses, 'Defenses', defensesDisplay); return false;"><xsl:value-of select="$loc/strs/characterSheet/str[@id='armory.character-sheet.defenses.display']"/><img id="checkLeftDefenses" src="/images/icons/icon-check.gif" style="visibility: hidden;" class="checkmark" /></a></li>
   </ul>
   </td><td class="r" /></tr><tr><td class="bl" /><td class="b" /><td class="br" /></tr> </table>
  </div>
 </div>

<xsl:call-template name="dropdownMenu">
  <xsl:with-param name="hiddenId" select="'Right'"/>
  <xsl:with-param name="defaultValue" select="$loc/strs/characterSheet/str[@id='armory.character-sheet.base-stats.display']" />
  <xsl:with-param name="divClass" select="'dropdown2'" />
  <xsl:with-param name="anchorClass" select="'profile-stats'" />
</xsl:call-template>

   <div class="drop-stats" style="display: none; z-index: 9999999; left: 190px;" id="dropdownHiddenRight" onMouseOver="javascript: varOverRight=1;" onMouseOut="javascript: varOverRight=0;">
  <div class="tooltip">
  <table><tr><td class="tl" /><td class="t" /><td class="tr" /></tr><tr><td class="l" /><td class="bg">
   <ul>
    <li><a href="#" onClick="changeStats('Right', replaceStringBaseStats, 'BaseStats', baseStatsDisplay); return false;"><xsl:value-of select="$loc/strs/characterSheet/str[@id='armory.character-sheet.base-stats.display']"/><img id="checkRightBaseStats" src="/images/icons/icon-check.gif" style="visibility: hidden;" class="checkmark" /></a></li>
	<li><a href="#" onClick="changeStats('Right', replaceStringMelee, 'Melee', meleeDisplay); return false;"><xsl:value-of select="$loc/strs/characterSheet/str[@id='armory.character-sheet.melee.display']"/><img id="checkRightMelee" src="/images/icons/icon-check.gif" style="visibility: hidden;" class="checkmark" /></a></li>
	<li><a href="#" onClick="changeStats('Right', replaceStringRanged, 'Ranged', rangedDisplay); return false;"><xsl:value-of select="$loc/strs/characterSheet/str[@id='armory.character-sheet.ranged.display']"/><img id="checkRightRanged" src="/images/icons/icon-check.gif" style="visibility: hidden;" class="checkmark" /></a></li>
	<li><a href="#" onClick="changeStats('Right', replaceStringSpell, 'Spell', spellDisplay); return false;"><xsl:value-of select="$loc/strs/characterSheet/str[@id='armory.character-sheet.spell.display']"/><img id="checkRightSpell" src="/images/icons/icon-check.gif" style="visibility: hidden;" class="checkmark" /></a></li>
	<li><a href="#" onClick="changeStats('Right', replaceStringDefenses, 'Defenses', defensesDisplay); return false;"><xsl:value-of select="$loc/strs/characterSheet/str[@id='armory.character-sheet.defenses.display']"/><img id="checkRightDefenses" src="/images/icons/icon-check.gif" style="visibility: hidden;" class="checkmark" /></a></li>
   </ul>
   </td><td class="r" /></tr><tr><td class="bl" /><td class="b" /><td class="br" /></tr></table>
  </div>
 </div><!--/drop-stats/-->

 <div class="stats1">
  <em class="ptl"><xsl:comment/></em><em class="ptr"><xsl:comment/></em><em class="pbl"><xsl:comment/></em><em class="pbr"><xsl:comment/></em>
  <div class="character-stats">
   <div id="replaceStatsLeft"></div>

  </div>
 </div>
 <div class="stats2">
  <em class="ptl"><xsl:comment/></em><em class="ptr"><xsl:comment/></em><em class="pbl"><xsl:comment/></em><em class="pbr"><xsl:comment/></em>
  <div class="character-stats">
   <div id="replaceStatsRight"></div>
<script type="text/javascript" src="js/character/textObjects.js"></script>

  </div>
 </div>
</div><!--/stack3/-->
<div class="stack4">
 <div class="items-bot">
   <ul>
   	<li id="_slot15"><em id="{$pathMainHand/@icon}" class="charSheetImg {$pathMainHand/@icon}"></em><a class="staticTip itemToolTip thisTip" id="{$pathMainHand/@id}"></a></li>
    <li id="_slot16"><em id="{$pathOffHand/@icon}" class="charSheetImg {$pathOffHand/@icon}"></em><a class="staticTip itemToolTip thisTip" id="{$pathOffHand/@id}"></a></li>
    <li id="_slot17">
    <u>
        <xsl:attribute name="class">
          <xsl:choose>  
            <xsl:when test = "$theClassId = 11 or $theClassId = 2 or $theClassId = 7 or $theClassId = 6">relic</xsl:when>
          </xsl:choose>
        </xsl:attribute>
        </u>    
    	<em id="{$pathRanged/@icon}" class="charSheetImg {$pathRanged/@icon}"></em><a class="staticTip itemToolTip thisTip" id="{$pathRanged/@id}"></a>
	</li>
   </ul>
  </div>

<script type="text/javascript">
  var items=new itemsObject();
</script>


<xsl:if test="characterTab/quivers/quiver">
  <div class="ammo staticTip itemToolTip" id="{characterTab/quivers/quiver/@id}">
   <img src="/wow-icons/_images/43x43/{characterTab/quivers/quiver/@icon}.png"/><p></p>
  </div>
</xsl:if>


</div><!--/stack4/-->

<div class="lastModified"><span><xsl:value-of select="$loc/strs/general/str[@id='armory.character.lastModified']"/></span>&#160;<strong><xsl:value-of select="$lastModified" /></strong></div>

</div><!--/profile-master/-->

<xsl:variable name="team2path" select="character/arenaTeams/arenaTeam[@size=2]" />
<xsl:variable name="team3path" select="character/arenaTeams/arenaTeam[@size=3]" />
<xsl:variable name="team5path" select="character/arenaTeams/arenaTeam[@size=5]" />

<div class="bonus-stats">
<xsl:attribute name="style">
<xsl:if test="$isPropass='true' or (characterTab/pvp/lifetimehonorablekills/@value &lt; 1337) and (string-length($team2path)=0) and (string-length($team3path)=0) and (string-length($team5path)=0)">display: none;</xsl:if>
</xsl:attribute>

<table class="deco-frame">
	<thead>
  	<tr><td class="sl"></td><td class="ct st"></td><td class="sr"></td></tr>
	</thead>
	<tbody>
		<tr><td class="sl"><b><em class="port"></em></b></td><td class="ct">
    <div class="bonus-stats-content">
     <div>
	  <em class="b-title"></em>
       <div class="arena-ranking">

<xsl:choose>
<xsl:when test="character/arenaTeams">

<div id ="divAchievementArenaTeams">
<h2><a href="character-arenateams.xml?{$charUrl}" style="float:right; position:relative;"><xsl:value-of select="$loc/strs/unsorted/str[@id='armory.labels.morearenateams']"/></a><xsl:value-of select="$loc/strs/unsorted/str[@id='armory.labels.header.arena']"/></h2>



<xsl:variable name="team2ranking" select="$team2path/@ranking" />
<xsl:variable name="team3ranking" select="$team3path/@ranking" />
<xsl:variable name="team5ranking" select="$team5path/@ranking" />
<xsl:variable name="textRatingColon" select="$loc/strs/unsorted/str[@id='armory.labels.ratingcolon']" />


<ul class="badges-pvp">
 <li>
  <div>
	<xsl:attribute name="class">
	  <xsl:choose>
	    <xsl:when test="string-length($team2path)=0" >arena-team-faded</xsl:when>
	    <xsl:otherwise>arenacontainer</xsl:otherwise>
	  </xsl:choose>
	</xsl:attribute>

	<h4><xsl:value-of select="$loc/strs/unsorted/str[@id='armory.labels.2v2']"/></h4>
	<em><span><xsl:choose>
	<xsl:when test="string-length($team2path) != 0"><xsl:value-of select="$textRatingColon" /> <xsl:value-of select="$team2path/@rating" /></xsl:when>
	<xsl:otherwise><xsl:value-of select="$loc/strs/unsorted/str[@id='armory.labels.noteam']"/></xsl:otherwise>
	</xsl:choose></span></em>
	<div id="icon2v2team">
	<xsl:attribute name="onClick">
	  <xsl:if test="$team2path">javascript: window.location.href="team-info.xml?<xsl:value-of select="$team2path/@url" />"</xsl:if>
	</xsl:attribute>
    <xsl:attribute name="class">
    	<xsl:choose>
		    <xsl:when test="$team2path">icon staticTip</xsl:when>
    	    <xsl:otherwise>icon</xsl:otherwise>    
        </xsl:choose>
    </xsl:attribute>  
	<xsl:attribute name="onMouseOver">
	  <xsl:if test="$team2path">setTipText(tooltip2v2team);</xsl:if>
	</xsl:attribute>
	<xsl:attribute name="style">
	  <xsl:if test="$team2path">cursor: pointer;</xsl:if>
	</xsl:attribute>
	<img src="/images/pixel.gif" id="badgeBorder2v2team" class="p" border="0" />

<xsl:choose>
<xsl:when test="$team2path" >

<xsl:variable name="standing2Print">
	<xsl:choose>
	<xsl:when test="$team2ranking != 0 and $team2ranking &lt;= 1000">
	<xsl:value-of select="$team2ranking" /><xsl:call-template name="positionSuffix"><xsl:with-param name="pos" select="$team2ranking"/></xsl:call-template>
	</xsl:when>
	</xsl:choose>
</xsl:variable>

    <div class="rank-num" id="arenarank">
		<xsl:call-template name="flash">
		<xsl:with-param name="id" select="'arenarank'"/>
		<xsl:with-param name="src" select="'/images/rank.swf'"/>
		<xsl:with-param name="wmode" select="'transparent'"/>
		<xsl:with-param name="width" select="'100'"/>
		<xsl:with-param name="height" select="'40'"/>
		<xsl:with-param name="quality" select="'best'"/>
		<xsl:with-param name="flashvars" select="concat('rankNum=', $standing2Print)"/>
		</xsl:call-template>
	</div>
</xsl:when>
</xsl:choose>

	</div></div>
	</li>
	<li>
	<div>
	<xsl:attribute name="class">
	  <xsl:choose>
	    <xsl:when test="string-length($team3path)=0" >arena-team-faded</xsl:when>
	    <xsl:otherwise>arenacontainer</xsl:otherwise>
	  </xsl:choose>
	</xsl:attribute>

	<h4><xsl:value-of select="$loc/strs/unsorted/str[@id='armory.labels.3v3']"/></h4>
	<em><span><xsl:choose>
	<xsl:when test="string-length($team3path) != 0"><xsl:value-of select="$textRatingColon" /> <xsl:value-of select="$team3path/@rating" /></xsl:when>
	<xsl:otherwise><xsl:value-of select="$loc/strs/unsorted/str[@id='armory.labels.noteam']"/></xsl:otherwise>
	</xsl:choose></span></em>

	<div id="icon3v3team">
	<xsl:attribute name="onClick">
	  <xsl:choose>
	    <xsl:when test="$team3path">javascript: window.location.href="team-info.xml?<xsl:value-of select="$team3path/@url" />"</xsl:when>
	  </xsl:choose>
	</xsl:attribute>
    <xsl:attribute name="class">
    	<xsl:choose>
		    <xsl:when test="$team3path">icon staticTip</xsl:when>
    	    <xsl:otherwise>icon</xsl:otherwise>    
        </xsl:choose>
    </xsl:attribute>    
	<xsl:attribute name="onMouseOver">
	  <xsl:choose>
	    <xsl:when test="$team3path">setTipText(tooltip3v3team);</xsl:when>
        <xsl:otherwise>setTipText('');</xsl:otherwise>
	  </xsl:choose>
	</xsl:attribute>
	<xsl:attribute name="style">
	  <xsl:choose>
	    <xsl:when test="$team3path">cursor: pointer;</xsl:when>
	    <xsl:otherwise>cursor: default;</xsl:otherwise>
	  </xsl:choose>
	</xsl:attribute>

	<img src="/images/pixel.gif" id="badgeBorder3v3team" class="p" border="0"/>

<xsl:choose>
<xsl:when test="$team3path" >

<xsl:variable name="standing3Print">
	<xsl:choose>
	<xsl:when test="$team3ranking != 0 and $team3ranking &lt;= 1000">
	<xsl:value-of select="$team3ranking" /><xsl:call-template name="positionSuffix"><xsl:with-param name="pos" select="$team3ranking"/></xsl:call-template>
	</xsl:when>
	</xsl:choose>
</xsl:variable>

	<div class="rank-num" id="arenarank2">
		<xsl:call-template name="flash">
		<xsl:with-param name="id" select="'arenarank2'"/>
		<xsl:with-param name="src" select="'/images/rank.swf'"/>
		<xsl:with-param name="wmode" select="'transparent'"/>
		<xsl:with-param name="width" select="'100'"/>
		<xsl:with-param name="height" select="'40'"/>
		<xsl:with-param name="quality" select="'best'"/>
		<xsl:with-param name="flashvars" select="concat('rankNum=', $standing3Print)"/>
		</xsl:call-template>
	</div>
</xsl:when>
</xsl:choose>


  	</div>
	</div>
	</li>

	<li>
	<div>
	<xsl:attribute name="class">
	  <xsl:choose>
	    <xsl:when test="string-length($team5path)=0" >arena-team-faded</xsl:when>
	    <xsl:otherwise>arenacontainer</xsl:otherwise>
	  </xsl:choose>
	</xsl:attribute>

	<h4><xsl:value-of select="$loc/strs/unsorted/str[@id='armory.labels.5v5']"/></h4>
	<em><span><xsl:choose>
	<xsl:when test="string-length($team5path) != 0"><xsl:value-of select="$textRatingColon" /> <xsl:value-of select="$team5path/@rating" /></xsl:when>
	<xsl:otherwise><xsl:value-of select="$loc/strs/unsorted/str[@id='armory.labels.noteam']"/></xsl:otherwise>
	</xsl:choose></span></em>

	<div id="icon5v5team">
	<xsl:attribute name="onClick">
	  <xsl:choose>
	    <xsl:when test="$team5path" >javascript: window.location.href="team-info.xml?<xsl:value-of select="$team5path/@url" />"</xsl:when>
	  </xsl:choose>
	</xsl:attribute>
    <xsl:attribute name="class">
    	<xsl:choose>
		    <xsl:when test="$team5path">icon staticTip</xsl:when>
    	    <xsl:otherwise>icon</xsl:otherwise>    
        </xsl:choose>
    </xsl:attribute>  
	<xsl:attribute name="onMouseOver">
	  <xsl:choose>
	    <xsl:when test="$team5path">setTipText(tooltip5v5team);</xsl:when>
	  </xsl:choose>
	</xsl:attribute>
	<xsl:attribute name="style">
	  <xsl:choose>
	    <xsl:when test="$team5path" >cursor: pointer;</xsl:when>
	    <xsl:otherwise>cursor: default;</xsl:otherwise>
	  </xsl:choose>
	</xsl:attribute>
	<img src="/images/pixel.gif" id="badgeBorder5v5team" class="p" border="0"/>

<xsl:choose>
<xsl:when test="$team5path" >

<xsl:variable name="standing5Print">
	<xsl:choose>
	<xsl:when test="$team5ranking != 0 and $team5ranking &lt;= 1000 ">
	<xsl:value-of select="$team5ranking" /><xsl:call-template name="positionSuffix"><xsl:with-param name="pos" select="$team5ranking"/></xsl:call-template>
	</xsl:when>
	</xsl:choose>
</xsl:variable>

	<div class="rank-num" id="arenarank3">
		<xsl:call-template name="flash">
		<xsl:with-param name="id" select="'arenarank3'"/>
		<xsl:with-param name="src" select="'/images/rank.swf'"/>
		<xsl:with-param name="wmode" select="'transparent'"/>
		<xsl:with-param name="width" select="'100'"/>
		<xsl:with-param name="height" select="'40'"/>
		<xsl:with-param name="quality" select="'best'"/>
		<xsl:with-param name="flashvars" select="concat('rankNum=', $standing5Print)"/>
		</xsl:call-template>
	</div>
</xsl:when>
</xsl:choose>

	</div>
	</div>
	</li>
</ul>

<ul class="badges-pvp personalrating">
	<li><div><em><span>

	<xsl:if test="$team2path">
		<xsl:value-of select="$loc/strs/unsorted/str[@id='armory.labels.personalratingcolon']"/><xsl:value-of select="$team2path/members/character[@name=$theCharName]/@contribution" />
	</xsl:if>

	</span></em></div></li>
	<li><div><em><span>

	<xsl:if test="$team3path">
		<xsl:value-of select="$loc/strs/unsorted/str[@id='armory.labels.personalratingcolon']"/><xsl:value-of select="$team3path/members/character[@name=$theCharName]/@contribution" />
	</xsl:if>

	</span></em></div></li>
	<li><div><em><span>

	<xsl:if test="$team5path">
		<xsl:value-of select="$loc/strs/unsorted/str[@id='armory.labels.personalratingcolon']"/><xsl:value-of select="$team5path/members/character[@name=$theCharName]/@contribution" />
	</xsl:if>

	</span></em></div></li>
</ul>

</div>

<script type="text/javascript">

  var arenaTeamArray=new Array;
  <xsl:for-each select="character/arenaTeams/arenaTeam">
      arenaTeamArray[<xsl:value-of select="@size" />]=["<xsl:value-of select="@name" />", "<xsl:value-of select="@url" />", "<xsl:value-of select="@ranking" />", <xsl:value-of select="@seasonGamesWon" />, <xsl:value-of select="@rating" />];
  </xsl:for-each>

</script>

<script type="text/javascript" src="js/character/arenaTooltips.js"></script>

</xsl:when>
</xsl:choose>

<xsl:choose>
<xsl:when test="characterTab/pvp/lifetimehonorablekills/@value &gt;= 1337">
<h2><xsl:value-of select="$loc/strs/unsorted/str[@id='armory.labels.header.pvp']"/></h2>
	  <h3><xsl:value-of select="$loc/strs/unsorted/str[@id='armory.labels.hks']"/> <strong><xsl:value-of select="characterTab/pvp/lifetimehonorablekills/@value" /></strong></h3>
</xsl:when>
</xsl:choose>

<div style="clear:both;"></div></div>
<!--<div class="bonus-footer"><p><a href="">More Reputations</a></p><a class="page-button-forward"></a><a class="page-button-back back-off"></a></div>-->
		</div>
		</div><!--/bonus-stats-content/-->
  </td><td class="sr"><b><em class="star"></em></b></td></tr>
 </tbody>
 <tfoot>
  <tr><td class="sl"></td><td class="ct sb" align="center"><b><em class="foot"></em></b></td><td class="sr"></td></tr>
 </tfoot>
</table>
</div><!--/bonus-stats/-->

  </xsl:when>
  <xsl:otherwise>

  <script type="text/javascript">document.getElementById('divCharTabs').style.display="none"</script>


  <xsl:choose>
  <xsl:when test="/page/errorCode/@value= 'serverBusy'"><xsl:call-template name="serverBusy" /></xsl:when>
  <xsl:otherwise><xsl:call-template name="unavailable" /></xsl:otherwise>
  </xsl:choose>

  </xsl:otherwise>
  </xsl:choose>

</xsl:when>
</xsl:choose>

    </div></div></div></div></div><!--/end profile/-->
			</div><!--/end profile-wrapper/-->

</xsl:when>
  <xsl:when test="/page/characterInfo/@errCode='belowMinLevel'">

	<blockquote><b class="icharacters">
<h4><a href="team-search.xml"><xsl:value-of select="$loc/strs/character/str[@id='character-profiles']"/></a></h4>
<h3><xsl:value-of select="$loc/strs/character/str[@id='character-profiles']"/></h3></b></blockquote>

<script type="text/javascript">document.getElementById('divCharTabs').style.display="none"</script>

	<div class="under10">
	<div class="bonus-stats">
	<table class="deco-frame">
	<thead>
	 <tr><td class="sl"></td><td class="ct st"></td><td class="sr"></td></tr>
	</thead>
	<tbody>
	 <tr><td class="sl"><b><em class="port"></em></b></td><td class="ct">

	<div class="kids">
	<h2><span><xsl:value-of select="$loc/strs/characterSheet/str[@id='armory.character-sheet.prompt.levelTen']"/></span></h2>
	<div class="levelBar-wrapper">
	  <div></div>
	</div>
	</div>

	  </td><td class="sr"><b><em class="star"></em></b></td></tr>
	</tbody>
	<tfoot>
	 <tr><td class="sl"></td><td class="ct sb" align="center"><b><em class="foot"></em></b></td><td class="sr"></td></tr>
	</tfoot>
	</table>
	</div><!--/bonus-stats/-->
	</div>


  </xsl:when>
<xsl:otherwise>

<script type="text/javascript">
document.getElementById('divCharTabs').style.display="none";
</script>

<xsl:call-template name="errorSection" />

</xsl:otherwise>
</xsl:choose>
<!--end big if-->

</div>

</div><!--/end player-side/-->

<!--<div id="miniSearchElement">

<xsl:choose>
<xsl:when test="$isPropass='true' and $pathPropass">



		<xsl:call-template name="propassRight">
		  <xsl:with-param name="pathPropass" select="$pathPropass" />
		</xsl:call-template>




	</xsl:when>
	<xsl:otherwise>
		<xsl:call-template name="charSheetMiniSearchPanel">
		  <xsl:with-param name="searchNode" select="'guild'" />
		</xsl:call-template>
	</xsl:otherwise>
</xsl:choose>

</div>-->
<div class="clear"><xsl:comment/></div>
</div><!--/end list/-->
</div><!--/end mini-search-start-state/-->
<!--/end index content/-->
  </div>
 </div>
</div><!--/dataelement/-->


<script type="text/javascript" src="/js/mini-search-ajax.js"></script>
<script type="text/javascript" src="/js/character-info-ajax.js"></script>
<script type="text/javascript">
    if (characterInfoPageInstance) {
        characterInfoPageInstance.setXmlUrl("character-sheet.xml");
    }
</script>

</xsl:template>

</xsl:stylesheet>
