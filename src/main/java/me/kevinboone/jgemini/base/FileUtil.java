/*=========================================================================
  
  JGemini

  FileUtil 

  Various methods for handling files and MIME types. The MIME list
  is taken from the Apache MIME database, and is probably way, way too
  comprehensive for this application.
 
  Note that the JVM has built-in methods for guess a MIME type from 
  a filename but, so far as I know, none to do the opposite, so 
  that's what getDefaultExtension() is for.

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;
import java.util.*;
import java.text.*;
import java.io.*;
import java.net.*;

/** A collection of utility methods for parsing filenames and 
    guessing file contents. */
public class FileUtil 
  {
  public static final String MIME_APPLICATION_JSON = "application/json";
  public static final String MIME_APPLICATION_ZIP = "application/zip";
  public static final String MIME_APPLICATION_X_GZIP = "application/x-gzip";
  public static final String MIME_APPLICATION_TGZ = "application/tgz";
  public static final String MIME_APPLICATION_MSWORD = "application/msword";
  public static final String MIME_APPLICATION_MSWORD_2007 = 
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
  public static final String MIME_APPLICATION_VND_TEXT = 
    "application/vnd.oasis.opendocument.text";
  public static final String MIME_APPLICATION_POSTSCRIPT = 
    "application/postscript";
  public static final String MIME_APPLICATION_PDF = "application/pdf";
  public static final String MIME_APPLICATION_JNLP = "application/jnlp";
  public static final String MIME_APPLICATION_MAC_BINHEX40 =
    "application/mac-binhex40";
  public static final String MIME_APPLICATION_MAC_COMPACTPRO = 
    "application/mac-compactpro";
  public static final String MIME_APPLICATION_MATHML_XML = 
    "application/mathml+xml";
  public static final String MIME_APPLICATION_OCTET_STREAM = 
    "application/octet-stream";
  public static final String MIME_APPLICATION_ODA = 
    "application/oda";
  public static final String MIME_APPLICATION_RDF_XML = 
    "application/rdf+xml";
  public static final String MIME_APPLICATION_JAVA_ARCHIVE = 
    "application/java-archive";
  public static final String MIME_APPLICATION_RDF_SMIL = "application/smil";
  public static final String MIME_APPLICATION_SRGS = "application/srgs";
  public static final String MIME_APPLICATION_SRGS_XML = "application/srgs+xml";
  public static final String MIME_APPLICATION_VND_MIF = "application/vnd.mif";
  public static final String MIME_APPLICATION_VND_MSEXCEL = 
    "application/vnd.ms-excel";
  public static final String MIME_APPLICATION_VND_MSEXCEL_2007 = 
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  public static final String MIME_APPLICATION_VND_SPREADSHEET = 
    "application/vnd.oasis.opendocument.spreadsheet";
  public static final String MIME_APPLICATION_VND_MSPOWERPOINT = 
    "application/vnd.ms-powerpoint";
  public static final String MIME_APPLICATION_VND_RNREALMEDIA = 
    "application/vnd.rn-realmedia";
  public static final String MIME_APPLICATION_X_BCPIO = "application/x-bcpio";
  public static final String MIME_APPLICATION_X_CDLINK = "application/x-cdlink";
  public static final String MIME_APPLICATION_X_CHESS_PGN 
    = "application/x-chess-pgn";
  public static final String MIME_APPLICATION_X_CPIO = "application/x-cpio";
  public static final String MIME_APPLICATION_X_CSH = "application/x-csh";
  public static final String MIME_APPLICATION_X_DIRECTOR = 
    "application/x-director";
  public static final String MIME_APPLICATION_X_DVI = "application/x-dvi";
  public static final String MIME_APPLICATION_X_FUTURESPLASH = 
    "application/x-futuresplash";
  public static final String MIME_APPLICATION_X_GTAR = "application/x-gtar";
  public static final String MIME_APPLICATION_X_HDF = "application/x-hdf";
  public static final String MIME_APPLICATION_X_JAVASCRIPT = 
    "application/x-javascript";
  public static final String MIME_APPLICATION_X_KOAN = "application/x-koan";
  public static final String MIME_APPLICATION_X_LATEX = "application/x-latex";
  public static final String MIME_APPLICATION_X_NETCDF = "application/x-netcdf";
  public static final String MIME_APPLICATION_X_OGG = "application/x-ogg";
  public static final String MIME_APPLICATION_X_SH = "application/x-sh";
  public static final String MIME_APPLICATION_X_SHAR = "application/x-shar";
  public static final String MIME_APPLICATION_X_STUFFIT = 
    "application/x-stuffit";
  public static final String MIME_APPLICATION_X_SV4CPIO = 
    "application/x-sv4cpio";
  public static final String MIME_APPLICATION_X_SV4CRC = 
    "application/x-sv4crc";
  public static final String MIME_APPLICATION_X_TAR = "application/x-tar";
  public static final String MIME_APPLICATION_X_RAR_COMPRESSED = 
    "application/x-rar-compressed";
  public static final String MIME_APPLICATION_X_TCL = "application/x-tcl";
  public static final String MIME_APPLICATION_X_TEX = "application/x-tex";
  public static final String MIME_APPLICATION_X_TEXINFO = 
    "application/x-texinfo";
  public static final String MIME_APPLICATION_X_TROFF = "application/x-troff";
  public static final String MIME_APPLICATION_X_TROFF_MAN = 
    "application/x-troff-man";
  public static final String MIME_APPLICATION_X_TROFF_ME = 
    "application/x-troff-me";
  public static final String MIME_APPLICATION_X_TROFF_MS = 
    "application/x-troff-ms";
  public static final String MIME_APPLICATION_X_USTAR = 
    "application/x-ustar";
  public static final String MIME_APPLICATION_X_WAIS_SOURCE = 
    "application/x-wais-source";
  public static final String MIME_APPLICATION_VND_MOZZILLA_XUL_XML = 
    "application/vnd.mozilla.xul+xml";
  public static final String MIME_APPLICATION_XHTML_XML = 
    "application/xhtml+xml";
  public static final String MIME_APPLICATION_XSLT_XML = 
    "application/xslt+xml";
  public static final String MIME_APPLICATION_XML = 
    "application/xml";
  public static final String MIME_APPLICATION_XML_DTD = 
    "application/xml-dtd";
  public static final String MIME_IMAGE_BMP = "image/bmp";
  public static final String MIME_IMAGE_CGM = "image/cgm";
  public static final String MIME_IMAGE_GIF = "image/gif";
  public static final String MIME_IMAGE_IEF = "image/ief";
  public static final String MIME_IMAGE_JPEG = "image/jpeg";
  public static final String MIME_IMAGE_TIFF = "image/tiff";
  public static final String MIME_IMAGE_PNG = "image/png";
  public static final String MIME_IMAGE_SVG_XML = "image/svg+xml";
  public static final String MIME_IMAGE_VND_DJVU = "image/vnd.djvu";
  public static final String MIME_IMAGE_WAP_WBMP = "image/vnd.wap.wbmp";
  public static final String MIME_IMAGE_X_CMU_RASTER = "image/x-cmu-raster";
  public static final String MIME_IMAGE_X_ICON = "image/x-icon";
  public static final String MIME_IMAGE_X_PORTABLE_ANYMAP = 
    "image/x-portable-anymap";
  public static final String MIME_IMAGE_X_PORTABLE_BITMAP = 
    "image/x-portable-bitmap";
  public static final String MIME_IMAGE_X_PORTABLE_GRAYMAP = 
    "image/x-portable-graymap";
  public static final String MIME_IMAGE_X_PORTABLE_PIXMAP = 
    "image/x-portable-pixmap";
  public static final String MIME_IMAGE_X_RGB = "image/x-rgb";
  public static final String MIME_AUDIO_BASIC = "audio/basic";
  public static final String MIME_AUDIO_MIDI = "audio/midi";
  public static final String MIME_AUDIO_MPEG = "audio/mpeg";
  public static final String MIME_AUDIO_X_AIFF = "audio/x-aiff";
  public static final String MIME_AUDIO_X_MPEGURL = "audio/x-mpegurl";
  public static final String MIME_AUDIO_X_PN_REALAUDIO = 
    "audio/x-pn-realaudio";
  public static final String MIME_AUDIO_X_WAV = "audio/x-wav";
  public static final String MIME_CHEMICAL_X_PDB = "chemical/x-pdb";
  public static final String MIME_CHEMICAL_X_XYZ = "chemical/x-xyz";
  public static final String MIME_MODEL_IGES = "model/iges";
  public static final String MIME_MODEL_MESH = "model/mesh";
  public static final String MIME_MODEL_VRLM = "model/vrml";
  public static final String MIME_TEXT_GEMINI = "text/gemini";
  public static final String MIME_TEXT_PLAIN = "text/plain";
  public static final String MIME_TEXT_RICHTEXT = "text/richtext";
  public static final String MIME_TEXT_RTF = "text/rtf";
  public static final String MIME_TEXT_MARKDOWN = "text/markdown";
  public static final String MIME_TEXT_HTML = "text/html";
  public static final String MIME_TEXT_CALENDAR = "text/calendar";
  public static final String MIME_TEXT_CSS = "text/css";
  public static final String MIME_TEXT_SGML = "text/sgml";
  public static final String MIME_TEXT_TAB_SEPARATED_VALUES = 
    "text/tab-separated-values";
  public static final String MIME_TEXT_VND_WAP_XML = 
    "text/vnd.wap.wml";
  public static final String MIME_TEXT_VND_WAP_WMLSCRIPT = 
    "text/vnd.wap.wmlscript";
  public static final String MIME_TEXT_X_SETEXT = "text/x-setext";
  public static final String MIME_TEXT_X_COMPONENT = "text/x-component";
  public static final String MIME_VIDEO_QUICKTIME = "video/quicktime";
  public static final String MIME_VIDEO_MPEG = "video/mpeg";
  public static final String MIME_VIDEO_VND_MPEGURL = "video/vnd.mpegurl";
  public static final String MIME_VIDEO_X_MSVIDEO = "video/x-msvideo";
  public static final String MIME_VIDEO_X_MS_WMV = "video/x-ms-wmv";
  public static final String MIME_VIDEO_X_SGI_MOVIE = "video/x-sgi-movie";
  public static final String MIME_X_CONFERENCE_X_COOLTALK = 
    "x-conference/x-cooltalk";

  private static HashMap<String, String> extMapping;

  // Constants for file sizes
  private static final long K = 1024;
  private static final long M = K * K;
  private static final long G = M * K;
  private static final long T = G * K;
  private final static long[] dividers 
    = new long[] { T, G, M, K, 1 };
  private final static String[] units 
    = new String[] { "TB", "GB", "MB", "KB", "B" };

  private static StatusHandler statusHandler = StatusHandler.getInstance();
  private final static ResourceBundle messagesBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Messages");

  static 
    {
    extMapping = new HashMap<String, String>()
      {
      private void put1 (String key, String value) 
	{
	put(key, value); 
	}
	{
	put1(MIME_APPLICATION_VND_MOZZILLA_XUL_XML, "xul");
	put1(MIME_APPLICATION_JSON, "json");
	put1(MIME_X_CONFERENCE_X_COOLTALK, "ice");
	put1(MIME_VIDEO_X_SGI_MOVIE, "movie");
	put1(MIME_VIDEO_X_MSVIDEO, "avi");
	put1(MIME_VIDEO_X_MS_WMV, "wmv");
	put1(MIME_VIDEO_VND_MPEGURL, "m4u");
	put1(MIME_TEXT_X_COMPONENT, "htc");
	put1(MIME_TEXT_X_SETEXT, "etx");
	put1(MIME_TEXT_VND_WAP_WMLSCRIPT, "wmls");
	put1(MIME_TEXT_VND_WAP_XML, "wml");
	put1(MIME_TEXT_TAB_SEPARATED_VALUES, "tsv");
	put1(MIME_TEXT_SGML, "sgml");
	put1(MIME_TEXT_MARKDOWN, "md");
	put1(MIME_TEXT_CSS, "css");
	put1(MIME_TEXT_CALENDAR, "ics");
	put1(MIME_MODEL_VRLM, "vrlm");
	put1(MIME_MODEL_MESH, "mesh");
	put1(MIME_MODEL_IGES, "iges");
	put1(MIME_IMAGE_X_RGB, "rgb");
	put1(MIME_IMAGE_X_PORTABLE_PIXMAP, "ppm");
	put1(MIME_IMAGE_X_PORTABLE_GRAYMAP, "pgm");
	put1(MIME_IMAGE_X_PORTABLE_BITMAP, "pbm");
	put1(MIME_IMAGE_X_PORTABLE_ANYMAP, "pnm");
	put1(MIME_IMAGE_X_ICON, "ico");
	put1(MIME_IMAGE_X_CMU_RASTER, "ras");
	put1(MIME_IMAGE_WAP_WBMP, "wbmp");
	put1(MIME_IMAGE_VND_DJVU, "djvu");
	put1(MIME_IMAGE_SVG_XML, "svg");
	put1(MIME_IMAGE_IEF, "ief");
	put1(MIME_IMAGE_CGM, "cgm");
	put1(MIME_IMAGE_BMP, "bmp");
	put1(MIME_CHEMICAL_X_XYZ, "xyz");
	put1(MIME_CHEMICAL_X_PDB, "pdb");
	put1(MIME_AUDIO_X_PN_REALAUDIO, "ra");
	put1(MIME_AUDIO_X_MPEGURL, "m3u");
	put1(MIME_AUDIO_X_AIFF, "aiff");
	put1(MIME_AUDIO_MPEG, "mp3");
	put1(MIME_AUDIO_MIDI, "midi");
	put1(MIME_APPLICATION_XML_DTD, "dtd");
	put1(MIME_APPLICATION_XML, "xml");
	put1(MIME_APPLICATION_XSLT_XML, "xslt");
	put1(MIME_APPLICATION_XHTML_XML, "xhtml");
	put1(MIME_APPLICATION_X_WAIS_SOURCE, "src");
	put1(MIME_APPLICATION_X_USTAR, "ustar");
	put1(MIME_APPLICATION_X_TROFF_MS, "ms");
	put1(MIME_APPLICATION_X_TROFF_ME, "me");
	put1(MIME_APPLICATION_X_TROFF_MAN, "man");
	put1(MIME_APPLICATION_X_TROFF, "roff");
	put1(MIME_APPLICATION_X_TEXINFO, "texi");
	put1(MIME_APPLICATION_X_TEX, "tex");
	put1(MIME_APPLICATION_X_TCL, "tcl");
	put1(MIME_APPLICATION_X_SV4CRC, "sv4crc");
	put1(MIME_APPLICATION_X_SV4CPIO, "sv4cpio");
	put1(MIME_APPLICATION_X_STUFFIT, "sit");
	put1(MIME_APPLICATION_X_SHAR, "shar");
	put1(MIME_APPLICATION_X_SH, "sh");
	put1(MIME_APPLICATION_X_NETCDF, "cdf");
	put1(MIME_APPLICATION_X_LATEX, "latex");
	put1(MIME_APPLICATION_X_KOAN, "skm");
	put1(MIME_APPLICATION_X_JAVASCRIPT, "js");
	put1(MIME_APPLICATION_X_HDF, "hdf");
	put1(MIME_APPLICATION_X_GTAR, "gtar");
	put1(MIME_APPLICATION_X_FUTURESPLASH, "spl");
	put1(MIME_APPLICATION_X_DVI, "dvi");
	put1(MIME_APPLICATION_X_DIRECTOR, "dir");
	put1(MIME_APPLICATION_X_CSH, "csh");
	put1(MIME_APPLICATION_X_CPIO, "cpio");
	put1(MIME_APPLICATION_X_CHESS_PGN, "pgn");
	put1(MIME_APPLICATION_X_CDLINK, "vcd");
	put1(MIME_APPLICATION_X_BCPIO, "bcpio");
	put1(MIME_APPLICATION_VND_RNREALMEDIA, "rm");
	put1(MIME_APPLICATION_VND_MSPOWERPOINT, "ppt");
	put1(MIME_APPLICATION_VND_MIF, "mif");
	put1(MIME_APPLICATION_SRGS_XML, "grxml");
	put1(MIME_APPLICATION_SRGS, "gram");
	put1(MIME_APPLICATION_RDF_SMIL, "smil");
	put1(MIME_APPLICATION_RDF_XML, "rdf");
	put1(MIME_APPLICATION_X_OGG, "ogg");
	put1(MIME_APPLICATION_ODA, "oda");
	put1(MIME_APPLICATION_MATHML_XML, "mathml");
	put1(MIME_APPLICATION_MAC_COMPACTPRO, "cpt");
	put1(MIME_APPLICATION_MAC_BINHEX40, "hqx");
	put1(MIME_APPLICATION_JNLP, "jnlp");
	put1(MIME_TEXT_PLAIN, "txt");
	put1(MIME_TEXT_RTF, "rtf");
	put1(MIME_TEXT_RICHTEXT, "rtx");
	put1(MIME_TEXT_HTML, "html");
	put1(MIME_APPLICATION_ZIP, "zip");
	put1(MIME_APPLICATION_X_RAR_COMPRESSED, "rar");
	put1(MIME_APPLICATION_X_GZIP, "gzip");
	put1(MIME_APPLICATION_TGZ, "tgz");
	put1(MIME_APPLICATION_X_TAR, "tar");
	put1(MIME_IMAGE_GIF, "gif");
	put1(MIME_IMAGE_JPEG, "jpg");
	put1(MIME_IMAGE_TIFF, "tiff");
	put1(MIME_IMAGE_PNG, "png");
	put1(MIME_AUDIO_BASIC, "au");
	put1(MIME_AUDIO_X_WAV, "wav");
	put1(MIME_VIDEO_QUICKTIME, "mov");
	put1(MIME_VIDEO_MPEG, "mpg");
	put1(MIME_APPLICATION_MSWORD, "doc");
	put1(MIME_APPLICATION_MSWORD_2007, "docx");
	put1(MIME_APPLICATION_VND_TEXT, "odt");
	put1(MIME_APPLICATION_VND_MSEXCEL, "xls");
	put1(MIME_APPLICATION_VND_SPREADSHEET, "ods");
	put1(MIME_APPLICATION_POSTSCRIPT, "ps");
	put1(MIME_APPLICATION_PDF, "pdf");
	put1(MIME_APPLICATION_OCTET_STREAM, "exe");
	put1(MIME_APPLICATION_JAVA_ARCHIVE, "jar");
	put1(MIME_TEXT_GEMINI, "gmi");
	}
      };
    }

/*============================================================================

  appendStringToFile

============================================================================*/
  public static void appendStringToFile (String filename, String string)
      throws IOException
    {
    FileWriter fw = new FileWriter (filename, true);
    BufferedWriter bw = new BufferedWriter (fw);
    bw.write (string);
    bw.flush();
    bw.close();
    }

/*============================================================================

  byteArrayToFile

============================================================================*/
  public static void byteArrayToFile (File file, byte[] b)
      throws IOException
    {
    FileOutputStream fos = new FileOutputStream (file);
    fos.write (b);
    fos.close();
    }

/*============================================================================

  canHandleContent

============================================================================*/
  public static boolean canHandleContent (String urlStr, String mime) 
    {
    if (mime != null)
      {
      if (mime.startsWith ("text/gemini"))
        return true;
      if (mime.startsWith ("text/gophermap"))
	return true;
      else if (mime.startsWith ("text/plain"))
	return true;
      else if (mime.startsWith ("text/nex")) // Not a real MIME
	return true;
      else if (mime.startsWith ("text/markdown"))
	return true;
      else if (mime.startsWith ("application/atom+xml"))
	return true;
      else if (mime.startsWith ("text/xml"))
	return true; 
      else if (mime.startsWith ("image/"))
        return true;
      }

    if (urlStr != null)
      {
      if (urlStr.endsWith (".gmi"))
	return true;
      if (urlStr.endsWith (".gopher"))
	return true;
      else if (urlStr.endsWith (".txt"))
	return true;
      else if (urlStr.endsWith (".md"))
	return true;
      }

    return false;
    }

/*============================================================================

  getDefaultExtension 

============================================================================*/
  /** Get a suitable filename extension from a MIME type. If there is no
      mapping, return a default.
  */
  public static String getDefaultExtension (String mimeType) 
    {
    String ext = lookupExtension (mimeType);
    if (ext == null) 
      {
      ext = "unknown";
      }
    return ext;
    }

/*=========================================================================
  
  getEncodingFromMime

=========================================================================*/
  /** Extract an encoding name from a MIME type. If there is none, 
      return "UTF-8".
  */
  public static String getEncodingFromMime (String mime)
    {
    String[] args = mime.split (";");
    for (int i = 0; i < args.length; i++)
      {
      String arg = args[i].trim();
      if (arg.startsWith ("charset="))
        return arg.substring (8);
      }
    return "UTF-8"; 
    }

/*============================================================================

  formatNumber 

============================================================================*/
  /** This is a helper function for humanBytes(). */
  private static String formatNumber (final long value, final long divider,
      final String unit)
    {
    final double result =
	  divider > 1 ? (double) value / (double) divider : (double) value;
    return new DecimalFormat ("#,##0.#").format(result) + " " + unit;
    }

/*============================================================================

  guessMimeTypeFromFilename

============================================================================*/
  public static String guessMimeTypeFromFilename (String filename)
    {
    if (filename.endsWith (".gmi")) return "text/gemini";
    return URLConnection.guessContentTypeFromName (filename); 
    }

/*============================================================================

  humanBytes

  TODO: make suffixes internationalizable

============================================================================*/
  /** Returns a string representation of a byte count, e.g., "110 kB". 
  */
  public static String humanBytes (long value)
    {
    final long[] dividers = new long[] { T, G, M, K, 1 };
    final String[] units = new String[] { "TB", "GB", "MB", "KB", "B" };

    if (value < 0) return "" + value;

    String result = null;
    for (int i = 0; i < dividers.length; i++)
      {
      final long divider = dividers[i];
      if (value >= divider) 
        {
        result = formatNumber (value, divider, units[i]);
        break;
        }
      }
    return result; 
    }

/*============================================================================

  lookupExtension

============================================================================*/
  /** Get a suitable filename extension from a MIME type. If there is no
    mapping, return null
  */
  public static String lookupExtension (String mimeType) 
    {
    return extMapping.get (mimeType.toLowerCase());
    }

/*============================================================================

  readBufferedInputStreamFully 

  TODO: some sort of user progress indication

============================================================================*/
  /** Reads fully from the stream to a byte array
  */
  public static byte[] readBufferedInputStreamFully (BufferedInputStream bis)
      throws IOException
    {
    int totalRead = 0;
    int nRead;
    byte[] data = new byte[16384];
    ByteArrayOutputStream content_buffer = new ByteArrayOutputStream();

    while ((nRead = bis.read (data, 0, data.length)) != -1) 
      {
      try
        {
        Thread.sleep (1); // We need to get an InterruptedException if canceled
        }
      catch (InterruptedException e)
        {
        throw new IOException ("Interrupted");
        }
      content_buffer.write (data, 0, nRead);
      totalRead += nRead;
      if (totalRead > 1024)
        statusHandler.writeMessage (messagesBundle.getString ("loaded") + " " 
          + (totalRead / 1024) + " kb");
       }

    byte[] ret = content_buffer.toByteArray();
    content_buffer.close();
    return ret;
    }

/*============================================================================

  urlToByteArray 

  TODO: some sort of user progress indication

============================================================================*/
  /** Reads fully from the URL to a byte array. 
  */
  public static byte[] urlToByteArray (URL url)
      throws IOException
    {

    InputStream is = url.openConnection().getInputStream();

    ByteArrayOutputStream content_buffer = new ByteArrayOutputStream();

    int nRead;
    byte[] data = new byte[16384];

    while ((nRead = is.read (data, 0, data.length)) != -1) 
      {
      content_buffer.write (data, 0, nRead);
      }

    byte[] content = content_buffer.toByteArray();

    content_buffer.close();
    is.close();

    return content;
    }


  }
