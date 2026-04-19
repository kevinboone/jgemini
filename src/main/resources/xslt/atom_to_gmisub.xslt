<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:atom="http://www.w3.org/2005/Atom">

<xsl:output method="text" omit-xml-declaration="yes" encoding="utf-8" indent="no" />
<xsl:template match="/"> <xsl:variable name="title"> <xsl:choose> <xsl:when test="contains(atom:feed/atom:title, ' - ')"> <xsl:value-of select="substring-after(atom:feed/atom:title, ' - ')"/> </xsl:when> <xsl:otherwise> <xsl:value-of select="atom:feed/atom:title"/> </xsl:otherwise> </xsl:choose> </xsl:variable># <xsl:value-of select="$title"/>
  <xsl:text>&#10;</xsl:text>
  <xsl:for-each select="atom:feed/atom:entry">
    <xsl:text>=> </xsl:text>
	<xsl:value-of select="atom:link/@href"/>
    <xsl:text> </xsl:text>
    <xsl:variable name="entry_published" select="substring-before(atom:published,'T')"/>
    <xsl:variable name="entry_updated" select="substring-before(atom:updated,'T')"/>
    <xsl:if test="$entry_published != ''">
      <xsl:value-of select="$entry_published"/>
    </xsl:if>
    <xsl:if test="$entry_published = ''">
      <xsl:value-of select="$entry_updated"/>
    </xsl:if>
    <xsl:text> </xsl:text>
      <xsl:value-of select="atom:title"/>
    <xsl:text>&#10;</xsl:text>
  </xsl:for-each>
</xsl:template>
</xsl:stylesheet>


