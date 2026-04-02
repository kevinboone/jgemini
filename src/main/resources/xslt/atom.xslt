<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:atom="http://www.w3.org/2005/Atom">


<xsl:output method="html" encoding="utf-8" indent="yes" />
<xsl:template match="/">
<html lang="en">
  <xsl:variable name="title">
    <xsl:choose>
      <xsl:when test="contains(atom:feed/atom:title, ' - ')">
        <xsl:value-of select="substring-after(atom:feed/atom:title, ' - ')"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="atom:feed/atom:title"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <body>
    <h1><xsl:value-of select="$title"/></h1>
    <p>
      <i>This page has been converted from the original Atom feed, 
         for ease of viewing.</i>
    </p>
      <xsl:for-each select="atom:feed/atom:entry">
	<xsl:variable name="entry_published" select="substring-before(atom:published,'T')"/>
	<xsl:variable name="entry_updated" select="substring-before(atom:updated,'T')"/>
        <p>
	<xsl:if test="$entry_published != ''">
          <xsl:value-of select="$entry_published"/>
	</xsl:if>
	<xsl:if test="$entry_published = ''">
          <xsl:value-of select="$entry_updated"/>
	</xsl:if>
        <xsl:text> </xsl:text>
        <a>
          <xsl:attribute name="href">
            <xsl:value-of select="atom:link/@href"/>
	  </xsl:attribute>
	  <xsl:value-of select="atom:title"/>
	</a>
        </p>
      </xsl:for-each>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>


