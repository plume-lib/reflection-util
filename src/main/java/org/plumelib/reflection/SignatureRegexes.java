package org.plumelib.reflection;

import java.util.regex.Pattern;
import org.checkerframework.checker.regex.qual.Regex;

// This is defined outside the Checker Framework because it is generally useful, and so that it can
// be used in both the checker/ and framework/ subprojects of the Checker Framework.
/**
 * This class defines regular expressions for types supported by the <a
 * href="https://checkerframework.org/manual/#signature-checker">Signature String Checker</a>.
 */
public class SignatureRegexes {

  /** Do not instantiate this class. */
  private SignatureRegexes() {
    throw new Error("Do not instantiate");
  }

  // ///////////////////////////////////////////////////////////////////////////
  // Functions on regular expressions
  //

  /**
   * Create a capturing group.
   *
   * @param arg a regular expression
   * @return the argument wrapped in a capturing group
   */
  private static final @Regex String GROUPED(@Regex String arg) {
    return "(" + arg + ")";
  }

  /**
   * Create a regex matching zero or more of the given argument (Kleene star).
   *
   * @param arg a regular expression
   * @return the argument, repeated zero or more times
   */
  @SuppressWarnings("regex:return") // string concatenation
  private static final @Regex String ANY(@Regex String arg) {
    return GROUPED(arg) + "*";
  }

  /**
   * Create a regex that must match the entire string.
   *
   * @param arg a regular expression
   * @return the argument, made to match the entire string
   */
  private static final @Regex String ANCHORED(@Regex String arg) {
    return "^" + arg + "$";
  }

  /**
   * An ungrouped alternation.
   *
   * @param args regular expressions
   * @return a regex that matches any one of the arguments
   */
  @SuppressWarnings("regex:return") // string concatenation
  private static final @Regex String ALTERNATE(@Regex String... args) {
    return String.join("|", args);
  }

  /**
   * A grouped alternation.
   *
   * @param args regular expressions
   * @return a regex that matches any one of the arguments, wrapped in a capturing group
   */
  private static final @Regex String GROUPED_ALTERNATE(@Regex String... args) {
    return GROUPED(ALTERNATE(args));
  }

  // ///////////////////////////////////////////////////////////////////////////
  // Building blocks for regular expressions
  //

  /** An unanchored regex that matches keywords, except primitive types. */
  private static final @Regex String KEYWORD_NON_PRIMITIVE_TYPE =
      ALTERNATE(
          "abstract",
          "assert",
          // "boolean",
          "break",
          // "byte",
          "case",
          "catch",
          // "char",
          "class",
          "const",
          "continue",
          "default",
          "do",
          // "double",
          "else",
          "enum",
          "extends",
          "final",
          "finally",
          // "float",
          "for",
          "if",
          "goto",
          "implements",
          "import",
          "instanceof",
          // "int",
          "interface",
          // "long",
          "native",
          "new",
          "package",
          "private",
          "protected",
          "public",
          "return",
          // "short",
          "static",
          "strictfp",
          "super",
          "switch",
          "synchronized",
          "this",
          "throw",
          "throws",
          "transient",
          "try",
          "void",
          "volatile",
          "while");

  /** An unanchored regex that matches primitive types. */
  private static final @Regex String PRIMITIVE_TYPE =
      ALTERNATE("boolean", "byte", "char", "double", "float", "int", "long", "short");

  /** A regex that matches field descriptors for primitive types. */
  private static final @Regex String FD_PRIMITIVE = "[BCDFIJSZ]";

  /** An unanchored regex that matches keywords. */
  private static final @Regex String KEYWORD = KEYWORD_NON_PRIMITIVE_TYPE + "|" + PRIMITIVE_TYPE;

  /**
   * A regex that matches identifier tokens that are not identifiers (keywords, boolean literals,
   * and the null literal).
   */
  private static final @Regex String KEYWORD_OR_LITERAL =
      ALTERNATE(KEYWORD, "true", "false", "null");

  /** A regex that matches Java identifier tokens, as defined by the Java grammar. */
  private static final @Regex String IDENTIFIER_TOKEN = "[A-Za-z_$][A-Za-z_$0-9]*";

  /** A grouped regex that matches identifiers. */
  private static final @Regex String IDENTIFIER =
      "(?!(?:" + KEYWORD_OR_LITERAL + ")\\b)" + IDENTIFIER_TOKEN;

  /** An anchored regex that matches Identifier strings. */
  public static final @Regex String IDENTIFIER_OR_PRIMITIVE_TYPE =
      ALTERNATE(IDENTIFIER, PRIMITIVE_TYPE);

  /** An unanchored regex that matches DotSeparatedIdentifiers strings. */
  private static final @Regex String DOT_SEPARATED_IDENTIFIERS =
      IDENTIFIER + ANY("\\." + IDENTIFIER);

  /** An unanchored regex that matches slash-separated identifiers. */
  private static final @Regex String SLASH_SEPARATED_IDENTIFIERS =
      IDENTIFIER + ANY("/" + IDENTIFIER);

  /** A regex that matches the nested-class part of a class name, for one nested class. */
  private static final @Regex String NESTED_ONE = "\\$[A-Za-z_0-9]+";

  /** A regex that matches the nested-class part of a class name. */
  private static final @Regex String NESTED = ANY(NESTED_ONE);

  /** An unanchored regex that matches BinaryName strings. */
  private static final @Regex String BINARY_NAME = DOT_SEPARATED_IDENTIFIERS + NESTED;

  /** A regex that matches the nested-class part of a class name. */
  private static final @Regex String ARRAY = "(\\[\\])*";

  /** A regex that matches InternalForm strings. */
  public static final @Regex String INTERNAL_FORM = SLASH_SEPARATED_IDENTIFIERS + NESTED;

  /** A regex that matches ClassGetName, for non-primitive, non-array types. */
  private static final @Regex String CLASS_GET_NAME_NONPRIMITIVE_NONARRAY =
      IDENTIFIER + "(\\." + IDENTIFIER + "|" + NESTED_ONE + ")*";

  // //////////////////////////////////////////////////////////////////////
  // Regexes and patterns for literal Strings, one per annotation definition.

  // Creating all the patterns at load time is a bit inefficient but is convenient for clients.

  /** An anchored regex that matches ArrayWithoutPackage strings. */
  public static final @Regex String ArrayWithoutPackageRegex =
      ANCHORED(GROUPED(IDENTIFIER_OR_PRIMITIVE_TYPE) + ARRAY);

  /** An anchored pattern that matches ArrayWithoutPackage strings. */
  public static final Pattern ArrayWithoutPackagePattern =
      Pattern.compile(ArrayWithoutPackageRegex);

  /** An anchored regex that matches BinaryName strings. */
  public static final @Regex String BinaryNameRegex = ANCHORED(BINARY_NAME);

  /** An anchored pattern that matches BinaryName strings. */
  public static final Pattern BinaryNamePattern = Pattern.compile(BinaryNameRegex);

  /** An anchored regex that matches BinaryNameWithoutPackage strings. */
  public static final @Regex String BinaryNameWithoutPackageRegex = ANCHORED(IDENTIFIER + NESTED);

  /** An anchored pattern that matches BinaryNameWithoutPackage strings. */
  public static final Pattern BinaryNameWithoutPackagePattern =
      Pattern.compile(BinaryNameWithoutPackageRegex);

  /** An anchored regex that matches BinaryNameOrPrimitiveType strings. */
  public static final @Regex String BinaryNameOrPrimitiveTypeRegex =
      ANCHORED(GROUPED_ALTERNATE(BINARY_NAME, PRIMITIVE_TYPE));

  /** An anchored pattern that matches BinaryNameOrPrimitiveType strings. */
  public static final Pattern BinaryNameOrPrimitiveTypePattern =
      Pattern.compile(BinaryNameOrPrimitiveTypeRegex);

  /** An anchored regex that matches ClassGetName strings. */
  public static final @Regex String ClassGetNameRegex =
      ANCHORED(
          GROUPED_ALTERNATE(
              // non-array
              PRIMITIVE_TYPE,
              CLASS_GET_NAME_NONPRIMITIVE_NONARRAY,
              // array
              ("\\[+"
                  + GROUPED_ALTERNATE(
                      FD_PRIMITIVE, "L" + CLASS_GET_NAME_NONPRIMITIVE_NONARRAY + ";"))));

  /** An anchored pattern that matches ClassGetName strings. */
  public static final Pattern ClassGetNamePattern = Pattern.compile(ClassGetNameRegex);

  /** An anchored regex that matches ClassGetSimpleName strings. */
  public static final @Regex String ClassGetSimpleNameRegex =
      ANCHORED(
          GROUPED_ALTERNATE(
                  "", // empty string is a ClassGetSimpleName
                  IDENTIFIER_OR_PRIMITIVE_TYPE)
              + ARRAY);

  /** An anchored pattern that matches ClassGetSimpleName strings. */
  public static final Pattern ClassGetSimpleNamePattern = Pattern.compile(ClassGetSimpleNameRegex);

  /** An anchored regex that matches DotSeparatedIdentifiers strings. */
  public static final @Regex String DotSeparatedIdentifiersRegex =
      ANCHORED(DOT_SEPARATED_IDENTIFIERS);

  /** An anchored pattern that matches DotSeparatedIdentifiers strings. */
  public static final Pattern DotSeparatedIdentifiersPattern =
      Pattern.compile(DotSeparatedIdentifiersRegex);

  /** An anchored regex that matches DotSeparatedIdentifiersOrPrimitiveType strings. */
  public static final @Regex String DotSeparatedIdentifiersOrPrimitiveTypeRegex =
      ANCHORED(GROUPED_ALTERNATE(DOT_SEPARATED_IDENTIFIERS, PRIMITIVE_TYPE));

  /** An anchored pattern that matches DotSeparatedIdentifiersOrPrimitiveType strings. */
  public static final Pattern DotSeparatedIdentifiersOrPrimitiveTypePattern =
      Pattern.compile(DotSeparatedIdentifiersOrPrimitiveTypeRegex);

  /** An anchored regex that matches FieldDescriptor strings. */
  public static final @Regex String FieldDescriptorRegex =
      ANCHORED("\\[*(" + FD_PRIMITIVE + "|L" + INTERNAL_FORM + ";)");

  /** An anchored pattern that matches FieldDescriptor strings. */
  public static final Pattern FieldDescriptorPattern = Pattern.compile(FieldDescriptorRegex);

  /** An anchored regex that matches FieldDescriptorWithoutPackage strings. */
  public static final @Regex String FieldDescriptorWithoutPackageRegex =
      ANCHORED("(" + FD_PRIMITIVE + "|\\[+" + FD_PRIMITIVE + "|\\[L" + IDENTIFIER + NESTED + ";)");

  /** An anchored pattern that matches FieldDescriptorWithoutPackage strings. */
  public static final Pattern FieldDescriptorWithoutPackagePattern =
      Pattern.compile(FieldDescriptorWithoutPackageRegex);

  /** An anchored regex that matches FieldDescriptorForPrimitive strings. */
  public static final @Regex String FieldDescriptorForPrimitiveRegex = ANCHORED("^[BCDFIJSZ]$");

  /** An anchored pattern that matches FieldDescriptorForPrimitive strings. */
  public static final Pattern FieldDescriptorForPrimitivePattern =
      Pattern.compile(FieldDescriptorForPrimitiveRegex);

  /** An anchored regex that matches FqBinaryName strings. */
  public static final @Regex String FqBinaryNameRegex =
      ANCHORED("(" + PRIMITIVE_TYPE + "|" + BINARY_NAME + ")" + ARRAY);

  /** An anchored pattern that matches FqBinaryName strings. */
  public static final Pattern FqBinaryNamePattern = Pattern.compile(FqBinaryNameRegex);

  /** An anchored regex that matches FullyQualifiedName strings. */
  public static final @Regex String FullyQualifiedNameRegex =
      ANCHORED("(" + PRIMITIVE_TYPE + "|" + DOT_SEPARATED_IDENTIFIERS + ")" + ARRAY);

  /** An anchored pattern that matches FullyQualifiedName strings. */
  public static final Pattern FullyQualifiedNamePattern = Pattern.compile(FullyQualifiedNameRegex);

  /** An anchored regex that matches Identifier strings. */
  public static final @Regex String IdentifierRegex = ANCHORED(IDENTIFIER);

  /** An anchored pattern that matches Identifier strings. */
  public static final Pattern IdentifierPattern = Pattern.compile(IdentifierRegex);

  /** An anchored regex that matches IdentifierOrPrimitiveType strings. */
  public static final @Regex String IdentifierOrPrimitiveTypeRegex =
      ANCHORED(IDENTIFIER_OR_PRIMITIVE_TYPE);

  /** An anchored pattern that matches IdentifierOrPrimitiveType strings. */
  public static final Pattern IdentifierOrPrimitiveTypePattern =
      Pattern.compile(IdentifierOrPrimitiveTypeRegex);

  /** An anchored regex that matches InternalForm strings. */
  public static final @Regex String InternalFormRegex = ANCHORED(INTERNAL_FORM);

  /** An anchored pattern that matches InternalForm strings. */
  public static final Pattern InternalFormPattern = Pattern.compile(InternalFormRegex);

  /** An anchored regex that matches PrimitiveType strings. */
  public static final @Regex String PrimitiveTypeRegex = ANCHORED(PRIMITIVE_TYPE);

  /** An anchored pattern that matches PrimitiveType strings. */
  public static final Pattern PrimitiveTypePattern = Pattern.compile(PrimitiveTypeRegex);
}
