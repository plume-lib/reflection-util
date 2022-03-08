package org.plumelib.reflection;

import java.io.File;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.checkerframework.checker.index.qual.IndexFor;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signature.qual.ArrayWithoutPackage;
import org.checkerframework.checker.signature.qual.BinaryName;
import org.checkerframework.checker.signature.qual.BinaryNameOrPrimitiveType;
import org.checkerframework.checker.signature.qual.BinaryNameWithoutPackage;
import org.checkerframework.checker.signature.qual.ClassGetName;
import org.checkerframework.checker.signature.qual.ClassGetSimpleName;
import org.checkerframework.checker.signature.qual.DotSeparatedIdentifiers;
import org.checkerframework.checker.signature.qual.DotSeparatedIdentifiersOrPrimitiveType;
import org.checkerframework.checker.signature.qual.FieldDescriptor;
import org.checkerframework.checker.signature.qual.FieldDescriptorForPrimitive;
import org.checkerframework.checker.signature.qual.FieldDescriptorWithoutPackage;
import org.checkerframework.checker.signature.qual.FqBinaryName;
import org.checkerframework.checker.signature.qual.FullyQualifiedName;
import org.checkerframework.checker.signature.qual.Identifier;
import org.checkerframework.checker.signature.qual.IdentifierOrPrimitiveType;
import org.checkerframework.checker.signature.qual.InternalForm;
import org.checkerframework.checker.signature.qual.PrimitiveType;
import org.checkerframework.framework.qual.EnsuresQualifierIf;

// TODO: There are 6 major formats: https://checkerframework.org/manual/#signature-annotations
// This should convert among all of them.  But perhaps just add functionality as the need comes up.

/**
 * Conversion utilities between Java and JVM string formats, for types and signatures.
 *
 * <p>Also predicates for testing strings.
 */
public final class Signatures {

  /** The file-system-specific directory separator. */
  private static final String dirSep = File.separator;

  ///////////////////////////////////////////////////////////////////////////
  /// Accessing parts of types
  ///

  /**
   * Returns the element type for the given type name, which results from removing all the array
   * brackets.
   *
   * @param fqBinaryName "a fully-qualified binary name" ({@code @FqBinaryNome})
   * @return the base element type of the argument, with all array brackets stripped
   */
  @SuppressWarnings("signature") // @FqBinaryName = @ClassGetName plus optional array brackets
  public static @ClassGetName String getArrayElementType(@FqBinaryName String fqBinaryName) {
    int bracketPos = fqBinaryName.indexOf('[');
    if (bracketPos == -1) {
      return fqBinaryName;
    } else {
      return fqBinaryName.substring(0, bracketPos);
    }
  }

  /**
   * Given a filename ending with ".class", return the binary name of the class.
   *
   * @param classfilename the name of a classfile, relative to a directory on the CLASSPATH
   * @return the basename of the classfile
   */
  @SuppressWarnings("signature:return") // basename of a classfile is a Binaryname
  public static @BinaryName String classfilenameToBinaryName(String classfilename) {
    if (!classfilename.endsWith(".class")) {
      throw new IllegalArgumentException("Bad class file name: " + classfilename);
    }
    classfilename = classfilename.substring(0, classfilename.length() - 6);
    if (classfilename.startsWith("/") || classfilename.startsWith(dirSep)) {
      classfilename = classfilename.substring(1);
    }
    // This might misbehave for a Windows file whose name contains "/".
    return classfilename.replace("/", ".").replace(dirSep, ".");
  }

  /**
   * Given a filename ending with ".class", return the simple (unqualified) binary name of the
   * class.
   *
   * @param classfilename the name of a classfile
   * @return the basename of the classfile
   */
  @SuppressWarnings("signature:return") // basename of a classfile is a Binaryname
  public static @BinaryName String classfilenameToBaseName(String classfilename) {
    if (!classfilename.endsWith(".class")) {
      throw new IllegalArgumentException("Bad class file name: " + classfilename);
    }
    @SuppressWarnings("index:assignment") // "/" is not the last character
    @IndexFor("classfilename") int start = classfilename.lastIndexOf("/") + 1;
    int end = classfilename.length() - 6;
    return classfilename.substring(start, end);
  }

  ///////////////////////////////////////////////////////////////////////////
  /// String concatenations
  ///

  // These are not yet special-cased by the typechecker, so provide methods so clients don't have to
  // suppress warnings.

  /**
   * Given a package name and a class name, combine them to form a qualified class name.
   *
   * @param packagename the package name
   * @param classname the class name
   * @return the qualified class name
   */
  public static @BinaryName String addPackage(
      @Nullable @DotSeparatedIdentifiers String packagename, @BinaryName String classname) {
    if (!isBinaryName(classname)) {
      throw new Error("Bad classname argument to addPackage: " + classname);
    }
    if (packagename == null) {
      return classname;
    } else {
      if (!isDotSeparatedIdentifiers(packagename)) {
        throw new Error("Bad packagename argument to addPackage: " + packagename);
      }
      @SuppressWarnings("signature:assignment") // string concatenation
      @BinaryName String result = packagename + "." + classname;
      return result;
    }
  }

  ///////////////////////////////////////////////////////////////////////////
  /// Type tests (predicates)
  ///

  /**
   * Returns true if the argument has the format of a ArrayWithoutPackage. The type it refers to
   * might or might not exist.
   *
   * @param s a string
   * @return true if the string is a @ArrayWithoutPackage
   */
  @SuppressWarnings("signature")
  @EnsuresQualifierIf(result = true, expression = "#1", qualifier = ArrayWithoutPackage.class)
  public static boolean isArrayWithoutPackage(String s) {
    return SignatureRegexes.ArrayWithoutPackagePattern.matcher(s).matches();
  }

  /**
   * Returns true if the argument has the format of a BinaryName. The type it refers to might or
   * might not exist.
   *
   * @param s a string
   * @return true if the string is a @BinaryName
   */
  @SuppressWarnings("signature")
  @EnsuresQualifierIf(result = true, expression = "#1", qualifier = BinaryName.class)
  public static boolean isBinaryName(String s) {
    return SignatureRegexes.BinaryNamePattern.matcher(s).matches();
  }

  /**
   * Returns true if the argument has the format of a BinaryNameWithoutPackage. The type it refers
   * to might or might not exist.
   *
   * @param s a string
   * @return true if the string is a @BinaryNameWithoutPackage
   */
  @SuppressWarnings("signature")
  @EnsuresQualifierIf(result = true, expression = "#1", qualifier = BinaryNameWithoutPackage.class)
  public static boolean isBinaryNameWithoutPackage(String s) {
    return SignatureRegexes.BinaryNameWithoutPackagePattern.matcher(s).matches();
  }

  /**
   * Returns true if the argument has the format of a BinaryNameOrPrimitiveType. The type it refers
   * to might or might not exist.
   *
   * @param s a string
   * @return true if the string is a @BinaryNameOrPrimitiveType
   */
  @SuppressWarnings("signature")
  @EnsuresQualifierIf(result = true, expression = "#1", qualifier = BinaryNameOrPrimitiveType.class)
  public static boolean isBinaryNameOrPrimitiveType(String s) {
    return SignatureRegexes.BinaryNameOrPrimitiveTypePattern.matcher(s).matches();
  }

  /**
   * Returns true if the argument has the format of a ClassGetName. The type it refers to might or
   * might not exist.
   *
   * @param s a string
   * @return true if the string is a @ClassGetName
   */
  @SuppressWarnings("signature")
  @EnsuresQualifierIf(result = true, expression = "#1", qualifier = ClassGetName.class)
  public static boolean isClassGetName(String s) {
    return SignatureRegexes.ClassGetNamePattern.matcher(s).matches();
  }

  /**
   * Returns true if the argument has the format of a ClassGetSimpleName. The type it refers to
   * might or might not exist.
   *
   * @param s a string
   * @return true if the string is a @ClassGetSimpleName
   */
  @SuppressWarnings("signature")
  @EnsuresQualifierIf(result = true, expression = "#1", qualifier = ClassGetSimpleName.class)
  public static boolean isClassGetSimpleName(String s) {
    return SignatureRegexes.ClassGetSimpleNamePattern.matcher(s).matches();
  }

  /**
   * Returns true if the argument has the format of a DotSeparatedIdentifiers. The package or type
   * it refers to might or might not exist.
   *
   * @param s a string
   * @return true if the string is a @DotSeparatedIdentifiers
   */
  @SuppressWarnings("signature")
  @EnsuresQualifierIf(result = true, expression = "#1", qualifier = DotSeparatedIdentifiers.class)
  public static boolean isDotSeparatedIdentifiers(String s) {
    return SignatureRegexes.DotSeparatedIdentifiersPattern.matcher(s).matches();
  }

  /**
   * Returns true if the argument has the format of a DotSeparatedIdentifiersOrPrimitiveType. The
   * package or type it refers to might or might not exist.
   *
   * @param s a string
   * @return true if the string is a @DotSeparatedIdentifiersOrPrimitiveType
   */
  @SuppressWarnings("signature")
  @EnsuresQualifierIf(
      result = true,
      expression = "#1",
      qualifier = DotSeparatedIdentifiersOrPrimitiveType.class)
  public static boolean isDotSeparatedIdentifiersOrPrimitiveType(String s) {
    return SignatureRegexes.DotSeparatedIdentifiersOrPrimitiveTypePattern.matcher(s).matches();
  }

  /**
   * Returns true if the argument has the format of a FieldDescriptor. The type it refers to might
   * or might not exist.
   *
   * @param s a string
   * @return true if the string is a @FieldDescriptor
   */
  @SuppressWarnings("signature")
  @EnsuresQualifierIf(result = true, expression = "#1", qualifier = FieldDescriptor.class)
  public static boolean isFieldDescriptor(String s) {
    return SignatureRegexes.FieldDescriptorPattern.matcher(s).matches();
  }

  /**
   * Returns true if the argument has the format of a FieldDescriptorWithoutPackage. The type it
   * refers to might or might not exist.
   *
   * @param s a string
   * @return true if the string is a @FieldDescriptorWithoutPackage
   */
  @SuppressWarnings("signature")
  @EnsuresQualifierIf(
      result = true,
      expression = "#1",
      qualifier = FieldDescriptorWithoutPackage.class)
  public static boolean isFieldDescriptorWithoutPackage(String s) {
    return SignatureRegexes.FieldDescriptorWithoutPackagePattern.matcher(s).matches();
  }

  /**
   * Returns true if the argument has the format of a FieldDescriptorForPrimitive.
   *
   * @param s a string
   * @return true if the string is a @FieldDescriptorForPrimitive
   */
  @SuppressWarnings("signature")
  @EnsuresQualifierIf(
      result = true,
      expression = "#1",
      qualifier = FieldDescriptorForPrimitive.class)
  public static boolean isFieldDescriptorForPrimitive(String s) {
    return SignatureRegexes.FieldDescriptorForPrimitivePattern.matcher(s).matches();
  }

  /**
   * Returns true if the argument has the format of a FqBinaryName. The type it refers to might or
   * might not exist.
   *
   * @param s a string
   * @return true if the string is a @FqBinaryName
   */
  @SuppressWarnings("signature")
  @EnsuresQualifierIf(result = true, expression = "#1", qualifier = FqBinaryName.class)
  public static boolean isFqBinaryName(String s) {
    return SignatureRegexes.FqBinaryNamePattern.matcher(s).matches();
  }

  /**
   * Returns true if the argument has the format of a FullyQualifiedName. The type it refers to
   * might or might not exist.
   *
   * @param s a string
   * @return true if the string is a @FullyQualifiedName
   */
  @SuppressWarnings("signature")
  @EnsuresQualifierIf(result = true, expression = "#1", qualifier = FullyQualifiedName.class)
  public static boolean isFullyQualifiedName(String s) {
    return SignatureRegexes.FullyQualifiedNamePattern.matcher(s).matches();
  }

  /**
   * Returns true if the argument has the format of a Identifier. The type it refers to might or
   * might not exist.
   *
   * @param s a string
   * @return true if the string is a @Identifier
   */
  @SuppressWarnings("signature")
  @EnsuresQualifierIf(result = true, expression = "#1", qualifier = Identifier.class)
  public static boolean isIdentifier(String s) {
    return SignatureRegexes.IdentifierPattern.matcher(s).matches();
  }

  /**
   * Returns true if the argument has the format of a IdentifierOrPrimitiveType. The type it refers
   * to might or might not exist.
   *
   * @param s a string
   * @return true if the string is a @IdentifierOrPrimitiveType
   */
  @SuppressWarnings("signature")
  @EnsuresQualifierIf(result = true, expression = "#1", qualifier = IdentifierOrPrimitiveType.class)
  public static boolean isIdentifierOrPrimitiveType(String s) {
    return SignatureRegexes.IdentifierOrPrimitiveTypePattern.matcher(s).matches();
  }

  /**
   * Returns true if the argument has the format of a InternalForm. The type it refers to might or
   * might not exist.
   *
   * @param s a string
   * @return true if the string is a @InternalForm
   */
  @SuppressWarnings("signature")
  @EnsuresQualifierIf(result = true, expression = "#1", qualifier = InternalForm.class)
  public static boolean isInternalForm(String s) {
    return SignatureRegexes.InternalFormPattern.matcher(s).matches();
  }

  /**
   * Returns true if the argument has the format of a PrimitiveType.
   *
   * @param s a string
   * @return true if the string is a @PrimitiveType
   */
  @SuppressWarnings("signature")
  @EnsuresQualifierIf(result = true, expression = "#1", qualifier = PrimitiveType.class)
  public static boolean isPrimitiveType(String s) {
    return SignatureRegexes.PrimitiveTypePattern.matcher(s).matches();
  }

  ///////////////////////////////////////////////////////////////////////////
  /// Type conversions
  ///

  /** Matches the "[][][]" at the end of a Java array type. */
  private static Pattern arrayBracketsPattern = Pattern.compile("(\\[\\])+$");

  /** A representation of an array: A pair of class name and the number of array dimensions. */
  public static class ClassnameAndDimensions {
    /** The class name. It is a binary name or a primitive. */
    public final @BinaryName String classname;
    /** The number of array dimensions. */
    public final int dimensions;
    /**
     * Create a new ClassnameAndDimensions.
     *
     * @param classname the class name: a binary name or a primitive
     * @param dimensions the number of array dimensions
     */
    public ClassnameAndDimensions(@BinaryName String classname, int dimensions) {
      this.classname = classname;
      this.dimensions = dimensions;
    }
    /**
     * Constructs a new ClassnameAndDimensions by parsing a fully-qualified binary name.
     *
     * @param typename the type name to parse, as a fully-qualified binary name (= fully-qualified
     *     name, but with $ separating outer from inner types)
     * @return the result of parsing the type name
     */
    public static ClassnameAndDimensions parseFqBinaryName(@FqBinaryName String typename) {
      Matcher m = arrayBracketsPattern.matcher(typename);
      @SuppressWarnings(
          "signature:assignment" // classname is a @ClassGetName for a non-array; equivalently, a
      // binary name for a non-array
      )
      @BinaryName String classname = m.replaceFirst("");
      int dimensions = (typename.length() - classname.length()) / 2;
      return new ClassnameAndDimensions(classname, dimensions);
    }
  }

  /** A map from Java primitive type name (such as "int") to field descriptor (such as "I"). */
  private static HashMap<@PrimitiveType String, @FieldDescriptor String>
      primitiveToFieldDescriptor = new HashMap<>(8);

  static {
    primitiveToFieldDescriptor.put("boolean", "Z");
    primitiveToFieldDescriptor.put("byte", "B");
    primitiveToFieldDescriptor.put("char", "C");
    primitiveToFieldDescriptor.put("double", "D");
    primitiveToFieldDescriptor.put("float", "F");
    primitiveToFieldDescriptor.put("int", "I");
    primitiveToFieldDescriptor.put("long", "J");
    primitiveToFieldDescriptor.put("short", "S");
  }

  /**
   * Convert a binary name to a field descriptor. For example, convert "java.lang.Object[]" to
   * "[Ljava/lang/Object;" or "int" to "I" or "pkg.Outer$Inner" to "Lpkg/Outer$Inner;".
   *
   * <p>There are no binary names for primitives or array types. Nonetheless, this method works for
   * them. It converts "java.lang.Object[]" to "[Ljava/lang/Object;" or "int" to "I".
   *
   * @param typename name of the type, in fully-qualified binary name format
   * @return name of the class, in field descriptor format
   */
  @SuppressWarnings("signature") // conversion routine
  public static @FieldDescriptor String binaryNameToFieldDescriptor(@FqBinaryName String typename) {
    ClassnameAndDimensions cad = ClassnameAndDimensions.parseFqBinaryName(typename);
    String result = primitiveToFieldDescriptor.get(cad.classname);
    if (result == null) {
      result = "L" + cad.classname + ";";
    }
    for (int i = 0; i < cad.dimensions; i++) {
      result = "[" + result;
    }
    return result.replace('.', '/');
  }

  /**
   * Convert a primitive Java type name (e.g., "int", "double", etc.) to a field descriptor (e.g.,
   * "I", "D", etc.).
   *
   * @param primitiveName name of the type, in Java format
   * @return name of the type, in field descriptor format
   * @throws IllegalArgumentException if primitiveName is not a valid primitive type name
   */
  public static @FieldDescriptor String primitiveTypeNameToFieldDescriptor(String primitiveName) {
    String result = primitiveToFieldDescriptor.get(primitiveName);
    if (result == null) {
      throw new IllegalArgumentException("Not the name of a primitive type: " + primitiveName);
    }
    return result;
  }

  /**
   * Convert from a BinaryName to the format of {@link Class#getName()}.
   *
   * <p>There are no binary names for primitives or array types. Nonetheless, this method works for
   * them. It converts "java.lang.Object[]" to "[Ljava.lang.Object;" or "int" to "int".
   *
   * @param bn the binary name to convert
   * @return the class name, in Class.getName format
   */
  @SuppressWarnings("signature") // conversion routine
  public static @ClassGetName String binaryNameToClassGetName(@BinaryName String bn) {
    if (bn.endsWith("[]")) {
      return binaryNameToFieldDescriptor(bn).replace('/', '.');
    } else {
      return bn;
    }
  }

  /**
   * Converts a binary name to a fully-qualified name.
   *
   * @param binaryName a type in binary name format
   * @return a fully-qualified name
   */
  @SuppressWarnings(
      "signature:return" // implementation bug. There are binary names for anonymous classes, but no
  // fully-qualified names for them.  Given a valid binary name "pkg.Outer$22", it produces
  // "pkg.Outer.22" which is not a valid fully-qualified name.
  )
  public static @FullyQualifiedName String binaryNameToFullyQualified(
      @BinaryName String binaryName) {
    return binaryName.replaceAll("\\$", ".");
  }

  /**
   * Convert from a FieldDescriptor to the format of {@link Class#getName()}.
   *
   * @param fd the class, in field descriptor format
   * @return the class name, in Class.getName format
   */
  @SuppressWarnings("signature") // conversion routine
  public static @ClassGetName String fieldDescriptorToClassGetName(@FieldDescriptor String fd) {
    if (fd.startsWith("[")) {
      return fd.replace('/', '.');
    } else {
      return fieldDescriptorToBinaryName(fd);
    }
  }

  /** A map from field descriptor (sach as "I") to Java primitive type (such as "int"). */
  private static HashMap<String, String> fieldDescriptorToPrimitive = new HashMap<>(8);

  static {
    fieldDescriptorToPrimitive.put("Z", "boolean");
    fieldDescriptorToPrimitive.put("B", "byte");
    fieldDescriptorToPrimitive.put("C", "char");
    fieldDescriptorToPrimitive.put("D", "double");
    fieldDescriptorToPrimitive.put("F", "float");
    fieldDescriptorToPrimitive.put("I", "int");
    fieldDescriptorToPrimitive.put("J", "long");
    fieldDescriptorToPrimitive.put("S", "short");
  }

  /** Matches the "[[[" prefix of a field descriptor for an array. */
  private static Pattern fdArrayBracketsPattern = Pattern.compile("^\\[+");

  // does not convert "V" to "void".  Should it?
  /**
   * Convert a field descriptor to a binary name. For example, convert "[Ljava/lang/Object;" to
   * "java.lang.Object[]" or "I" to "int".
   *
   * @param typename name of the type, in JVML format
   * @return name of the type, in Java format
   */
  @SuppressWarnings("signature") // conversion routine
  public static @BinaryName String fieldDescriptorToBinaryName(String typename) {
    if (typename.equals("")) {
      throw new Error("Empty string passed to fieldDescriptorToBinaryName");
    }
    Matcher m = fdArrayBracketsPattern.matcher(typename);
    String classname = m.replaceFirst("");
    int dimensions = typename.length() - classname.length();
    String result;
    if (classname.startsWith("L") && classname.endsWith(";")) {
      result = classname.substring(1, classname.length() - 1);
    } else {
      result = fieldDescriptorToPrimitive.get(classname);
      if (result == null) {
        throw new Error("Malformed base class: " + classname);
      }
    }
    for (int i = 0; i < dimensions; i++) {
      result += "[]";
    }
    return result.replace('/', '.');
  }

  /**
   * Given a class name in internal form, return it in ClassGetName form.
   *
   * @param internalForm a class name in internal form
   * @return the class name in ClassGetName form
   */
  public static @ClassGetName String internalFormToClassGetName(@InternalForm String internalForm) {
    return internalForm.replace('/', '.');
  }

  /**
   * Given a class name in internal form, return it in as a binary name.
   *
   * @param internalForm a class name in internal form
   * @return the class name sa a binary name
   */
  public static @BinaryName String internalFormToBinaryName(@InternalForm String internalForm) {
    return internalForm.replace('/', '.');
  }

  /**
   * Given a class name in internal form, return it in as a fully-qualified name.
   *
   * @param internalForm a type in internal form
   * @return a fully-qualified name
   */
  public static @FullyQualifiedName String internalFormToFullyQualified(
      @InternalForm String internalForm) {
    return binaryNameToFullyQualified(internalFormToBinaryName(internalForm));
  }

  ///////////////////////////////////////////////////////////////////////////
  /// Method signatures, which combine multiple types
  ///

  /**
   * Convert a fully-qualified argument list from Java format to JVML format. For example, convert
   * "(java.lang.Integer[], int, java.lang.Integer[][])" to
   * "([Ljava/lang/Integer;I[[Ljava/lang/Integer;)".
   *
   * @param arglist an argument list, in Java format
   * @return argument list, in JVML format
   */
  public static String arglistToJvm(String arglist) {
    if (!(arglist.startsWith("(") && arglist.endsWith(")"))) {
      throw new Error("Malformed arglist: " + arglist);
    }
    String result = "(";
    String commaSepArgs = arglist.substring(1, arglist.length() - 1);
    StringTokenizer argsTokenizer = new StringTokenizer(commaSepArgs, ",", false);
    while (argsTokenizer.hasMoreTokens()) {
      @SuppressWarnings("signature") // substring
      @BinaryName String arg = argsTokenizer.nextToken().trim();
      result += binaryNameToFieldDescriptor(arg);
    }
    result += ")";
    // System.out.println("arglistToJvm: " + arglist + " => " + result);
    return result;
  }

  /**
   * Convert an argument list from JVML format to Java format. For example, convert
   * "([Ljava/lang/Integer;I[[Ljava/lang/Integer;)" to "(java.lang.Integer[], int,
   * java.lang.Integer[][])".
   *
   * @param arglist an argument list, in JVML format
   * @return argument list, in Java format
   */
  public static String arglistFromJvm(String arglist) {
    if (!(arglist.startsWith("(") && arglist.endsWith(")"))) {
      throw new Error("Malformed arglist: " + arglist);
    }
    String result = "(";
    @Positive int pos = 1;
    while (pos < arglist.length() - 1) {
      if (pos > 1) {
        result += ", ";
      }
      int nonarrayPos = pos;
      while (arglist.charAt(nonarrayPos) == '[') {
        nonarrayPos++;
        if (nonarrayPos >= arglist.length()) {
          throw new Error("Malformed arglist: " + arglist);
        }
      }
      char c = arglist.charAt(nonarrayPos);
      if (c == 'L') {
        int semicolonPos = arglist.indexOf(';', nonarrayPos);
        if (semicolonPos == -1) {
          throw new Error("Malformed arglist: " + arglist);
        }
        String fieldDescriptor = arglist.substring(pos, semicolonPos + 1);
        result += fieldDescriptorToBinaryName(fieldDescriptor);
        pos = semicolonPos + 1;
      } else {
        String maybe = fieldDescriptorToBinaryName(arglist.substring(pos, nonarrayPos + 1));
        if (maybe == null) {
          // return null;
          throw new Error("Malformed arglist: " + arglist);
        }
        result += maybe;
        pos = nonarrayPos + 1;
      }
    }
    return result + ")";
  }
}
