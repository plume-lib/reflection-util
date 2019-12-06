package org.plumelib.reflection;

import static org.junit.Assert.assertTrue;

import org.checkerframework.checker.signature.qual.BinaryName;
import org.checkerframework.checker.signature.qual.ClassGetName;
import org.checkerframework.checker.signature.qual.FieldDescriptor;
import org.checkerframework.checker.signature.qual.FullyQualifiedName;
import org.junit.Test;

/** Test code for the Signatures class. */
@SuppressWarnings({
  "UseCorrectAssertInTests" // I don't see the problem with using `assert`
})
public final class TestSignatures {

  ///////////////////////////////////////////////////////////////////////////
  /// Accessing parts of types
  ///

  /**
   * Returns the element type for the given type name, which results from removing all the array
   * brackets.
   */
  @Test
  public void testGetArrayElementType() {
    assertTrue(Signatures.getArrayElementType("int[][][]").equals("int"));
    assertTrue(Signatures.getArrayElementType("int").equals("int"));
  }

  /** Given a filename ending with ".class", return the class name. */
  @Test
  public void testClassfilenameToBinaryName() {
    assertTrue(Signatures.classfilenameToBinaryName("/foo/bar/baz/Quux.class").equals("Quux"));
    assertTrue(Signatures.classfilenameToBinaryName("Quux.class").equals("Quux"));
    assertTrue(
        Signatures.classfilenameToBinaryName("/foo/bar/baz/Quux$22.class").equals("Quux$22"));
    assertTrue(Signatures.classfilenameToBinaryName("Quux$22.class").equals("Quux$22"));
  }

  ///////////////////////////////////////////////////////////////////////////
  /// String concatenations
  ///

  /*
   * Given a package name and a class name, combine them to form a qualified class name.
   */
  @Test
  public void testAddPackage() {
    assertTrue(Signatures.addPackage(null, "Foo").equals("Foo"));
    assertTrue(Signatures.addPackage("a.b.c", "Foo").equals("a.b.c.Foo"));
  }

  ///////////////////////////////////////////////////////////////////////////
  /// Type tests
  ///

  /**
   * Returns true if the argument has the format of a ClassGetName. The type it refers to might or
   * might not exist.
   */
  @Test
  public void testIsClassGetName() {
    assertTrue(Signatures.isClassGetName("int"));
    assertTrue(Signatures.isClassGetName("[[I"));
    assertTrue(!Signatures.isClassGetName("int[][]"));
    assertTrue(Signatures.isClassGetName("java.lang.String"));
    assertTrue(!Signatures.isClassGetName("java.lang.String[]"));
    assertTrue(Signatures.isClassGetName("MyClass$22"));
    assertTrue(!Signatures.isClassGetName("MyClass$22[]"));
    assertTrue(Signatures.isClassGetName("pkg.Outer$Inner"));
    assertTrue(!Signatures.isClassGetName("pkg.Outer$Inner[]"));
    assertTrue(Signatures.isClassGetName("[LMyClass;"));
    assertTrue(Signatures.isClassGetName("MyClass$22"));
    // Commented out due to bug in Checker Framework.  Enable after CF version 3.0 is released.
    // assertTrue(Signatures.isClassGetName("[LMyClass$22;"));
    assertTrue(Signatures.isClassGetName("java.lang.Integer"));
    assertTrue(Signatures.isClassGetName("[Ljava.lang.Integer;"));
    assertTrue(Signatures.isClassGetName("pkg.Outer$Inner"));
    assertTrue(Signatures.isClassGetName("[Lpkg.Outer$Inner;"));
    assertTrue(Signatures.isClassGetName("pkg.Outer$22"));
    assertTrue(Signatures.isClassGetName("[Lpkg.Outer$22;"));
  }

  /**
   * Returns true if the argument has the format of a BinaryName. The type it refers to might or
   * might not exist.
   */
  @Test
  public void testIsBinaryName() {
    assertTrue(Signatures.isBinaryName("int"));
    assertTrue(!Signatures.isBinaryName("int[][]"));
    assertTrue(Signatures.isBinaryName("java.lang.String"));
    assertTrue(!Signatures.isBinaryName("java.lang.String[]"));
    assertTrue(Signatures.isBinaryName("MyClass$22"));
    assertTrue(!Signatures.isBinaryName("MyClass$22[]"));
    assertTrue(Signatures.isBinaryName("pkg.Outer$Inner"));
    assertTrue(!Signatures.isBinaryName("pkg.Outer$Inner[]"));
  }

  /**
   * Returns true if the argument has the format of a FqBinaryName. The type it refers to might or
   * might not exist.
   */
  @Test
  public void testIsFqBinaryName() {
    assertTrue(!Signatures.isFqBinaryName("hello world"));
    assertTrue(!Signatures.isFqBinaryName("[[I"));
    assertTrue(Signatures.isFqBinaryName("int"));
    assertTrue(Signatures.isFqBinaryName("int[][]"));
    assertTrue(Signatures.isFqBinaryName("java.lang.String"));
    assertTrue(Signatures.isFqBinaryName("java.lang.String[]"));
    assertTrue(Signatures.isFqBinaryName("MyClass$22"));
    assertTrue(Signatures.isFqBinaryName("MyClass$22[]"));
    assertTrue(Signatures.isFqBinaryName("pkg.Outer$Inner"));
    assertTrue(Signatures.isFqBinaryName("pkg.Outer$Inner[]"));
  }

  /**
   * Returns true if the argument has the format of a DotSeparatedIdentifiers. The package or type
   * it refers to might or might not exist.
   */
  @Test
  public void testIsDotSeparatedIdentifiers() {
    assertTrue(!Signatures.isDotSeparatedIdentifiers("hello world"));
    assertTrue(!Signatures.isDotSeparatedIdentifiers("[[I"));
    assertTrue(Signatures.isDotSeparatedIdentifiers("int"));
    assertTrue(!Signatures.isDotSeparatedIdentifiers("int[][]"));
    assertTrue(Signatures.isDotSeparatedIdentifiers("java.lang.String"));
    assertTrue(!Signatures.isDotSeparatedIdentifiers("java.lang.String[]"));
    assertTrue(!Signatures.isDotSeparatedIdentifiers("MyClass$22"));
    assertTrue(!Signatures.isDotSeparatedIdentifiers("MyClass$22[]"));
    assertTrue(!Signatures.isDotSeparatedIdentifiers("pkg.Outer$Inner"));
    assertTrue(!Signatures.isDotSeparatedIdentifiers("pkg.Outer$Inner[]"));
  }

  ///////////////////////////////////////////////////////////////////////////
  /// Type conversions
  ///

  @SuppressWarnings("ArrayEquals")
  @Test
  public void testConversions() {

    // public static String binaryNameToFieldDescriptor(String classname)
    assertTrue(Signatures.binaryNameToFieldDescriptor("boolean").equals("Z"));
    assertTrue(Signatures.binaryNameToFieldDescriptor("byte").equals("B"));
    assertTrue(Signatures.binaryNameToFieldDescriptor("char").equals("C"));
    assertTrue(Signatures.binaryNameToFieldDescriptor("double").equals("D"));
    assertTrue(Signatures.binaryNameToFieldDescriptor("float").equals("F"));
    assertTrue(Signatures.binaryNameToFieldDescriptor("int").equals("I"));
    assertTrue(Signatures.binaryNameToFieldDescriptor("long").equals("J"));
    assertTrue(Signatures.binaryNameToFieldDescriptor("short").equals("S"));
    assertTrue(Signatures.binaryNameToFieldDescriptor("Integer").equals("LInteger;"));
    assert Signatures.binaryNameToFieldDescriptor("Java.lang.Integer")
        .equals("LJava/lang/Integer;");
    assertTrue(Signatures.binaryNameToFieldDescriptor("int[][]").equals("[[I"));
    assert Signatures.binaryNameToFieldDescriptor("java.lang.Object[]")
        .equals("[Ljava/lang/Object;");

    // public static @ClassGetName String binaryNameToClassGetName(/*BinaryName*/ String bn)
    assertTrue(Signatures.binaryNameToClassGetName("boolean").equals("boolean"));
    assertTrue(Signatures.binaryNameToClassGetName("byte").equals("byte"));
    assertTrue(Signatures.binaryNameToClassGetName("char").equals("char"));
    assertTrue(Signatures.binaryNameToClassGetName("double").equals("double"));
    assertTrue(Signatures.binaryNameToClassGetName("float").equals("float"));
    assertTrue(Signatures.binaryNameToClassGetName("int").equals("int"));
    assertTrue(Signatures.binaryNameToClassGetName("long").equals("long"));
    assertTrue(Signatures.binaryNameToClassGetName("short").equals("short"));
    assertTrue(Signatures.binaryNameToClassGetName("Integer").equals("Integer"));
    assertTrue(
        Signatures.binaryNameToClassGetName("Java.lang.Integer").equals("Java.lang.Integer"));
    @SuppressWarnings("signature:assignment.type.incompatible") // test beyond the method's contract
    @BinaryName String intArrayAray = "int[][]", objectArray = "java.lang.Object[]";
    assertTrue(Signatures.binaryNameToClassGetName(intArrayAray).equals("[[I"));
    assertTrue(Signatures.binaryNameToClassGetName(objectArray).equals("[Ljava.lang.Object;"));

    // public static String fieldDescriptorToBinaryName(String classname)
    assertTrue(Signatures.fieldDescriptorToBinaryName("Z").equals("boolean"));
    assertTrue(Signatures.fieldDescriptorToBinaryName("B").equals("byte"));
    assertTrue(Signatures.fieldDescriptorToBinaryName("C").equals("char"));
    assertTrue(Signatures.fieldDescriptorToBinaryName("D").equals("double"));
    assertTrue(Signatures.fieldDescriptorToBinaryName("F").equals("float"));
    assertTrue(Signatures.fieldDescriptorToBinaryName("I").equals("int"));
    assertTrue(Signatures.fieldDescriptorToBinaryName("J").equals("long"));
    assertTrue(Signatures.fieldDescriptorToBinaryName("S").equals("short"));
    assertTrue(Signatures.fieldDescriptorToBinaryName("LInteger;").equals("Integer"));
    assert Signatures.fieldDescriptorToBinaryName("LJava/lang/Integer;")
        .equals("Java.lang.Integer");
    assertTrue(Signatures.fieldDescriptorToBinaryName("[[I").equals("int[][]"));
    assert Signatures.fieldDescriptorToBinaryName("[[LJava/lang/Integer;")
        .equals("Java.lang.Integer[][]");

    // public static @ClassGetName String
    //     fieldDescriptorToClassGetName(/*FieldDescriptor*/ String fd)
    assertTrue(Signatures.fieldDescriptorToClassGetName("Z").equals("boolean"));
    assertTrue(Signatures.fieldDescriptorToClassGetName("B").equals("byte"));
    assertTrue(Signatures.fieldDescriptorToClassGetName("C").equals("char"));
    assertTrue(Signatures.fieldDescriptorToClassGetName("D").equals("double"));
    assertTrue(Signatures.fieldDescriptorToClassGetName("F").equals("float"));
    assertTrue(Signatures.fieldDescriptorToClassGetName("I").equals("int"));
    assertTrue(Signatures.fieldDescriptorToClassGetName("J").equals("long"));
    assertTrue(Signatures.fieldDescriptorToClassGetName("S").equals("short"));
    assertTrue(Signatures.fieldDescriptorToClassGetName("LInteger;").equals("Integer"));
    assert Signatures.fieldDescriptorToClassGetName("LJava/lang/Integer;")
        .equals("Java.lang.Integer");
    assertTrue(Signatures.fieldDescriptorToClassGetName("[[I").equals("[[I"));
    assert Signatures.fieldDescriptorToClassGetName("[[LJava/lang/Integer;")
        .equals("[[LJava.lang.Integer;");

    assertTrue(Signatures.internalFormToClassGetName("MyClass").equals("MyClass"));
    assertTrue(Signatures.internalFormToClassGetName("MyClass$22").equals("MyClass$22"));
    assertTrue(
        Signatures.internalFormToClassGetName("java/lang/Integer").equals("java.lang.Integer"));
    assertTrue(Signatures.internalFormToClassGetName("pkg/Outer$Inner").equals("pkg.Outer$Inner"));
    assertTrue(Signatures.internalFormToClassGetName("pkg/Outer$22").equals("pkg.Outer$22"));

    assertTrue(Signatures.internalFormToBinaryName("MyClass").equals("MyClass"));
    assertTrue(Signatures.internalFormToBinaryName("MyClass$22").equals("MyClass$22"));
    assertTrue(
        Signatures.internalFormToBinaryName("java/lang/Integer").equals("java.lang.Integer"));
    assertTrue(Signatures.internalFormToBinaryName("pkg/Outer$Inner").equals("pkg.Outer$Inner"));
    assertTrue(Signatures.internalFormToBinaryName("pkg/Outer$22").equals("pkg.Outer$22"));

    // More tests for type representation conversions.
    // Table from Signature Checker manual.
    checkTypeStrings("int", "int", "int", "I");
    checkTypeStrings("MyClass", "MyClass", "MyClass", "LMyClass;");
    checkTypeStrings(
        "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "Ljava/lang/Integer;");
    checkTypeStrings(
        "java.lang.Byte.ByteCache",
        "java.lang.Byte$ByteCache",
        "java.lang.Byte$ByteCache",
        "Ljava/lang/Byte$ByteCache;");
  }

  // Tests implementation details of some routines that accept more arguments than claimed by their
  // specification.
  @SuppressWarnings({"ArrayEquals", "signature"})
  @Test
  public void testSignaturesImplementation() {

    // public static String binaryNameToFieldDescriptor(String classname)
    assertTrue(Signatures.binaryNameToFieldDescriptor("int[][]").equals("[[I"));
    assert Signatures.binaryNameToFieldDescriptor("Java.lang.Integer[][][]")
        .equals("[[[LJava/lang/Integer;");

    // public static @ClassGetName String binaryNameToClassGetName(/*BinaryName*/ String bn)
    assertTrue(Signatures.binaryNameToClassGetName("int[][]").equals("[[I"));
    assert Signatures.binaryNameToClassGetName("Java.lang.Integer[][][]")
        .equals("[[[LJava.lang.Integer;");

    // More tests for type representation conversions.
    // Table from Signature Checker manual.
    checkTypeStrings("int[][]", "int[][]", "[[I", "[[I");
    checkTypeStrings("MyClass[]", "MyClass[]", "[LMyClass;", "[LMyClass;");
    checkTypeStrings(
        "java.lang.Integer[]",
        "java.lang.Integer[]",
        "[Ljava.lang.Integer;",
        "[Ljava/lang/Integer;");
    checkTypeStrings(
        "java.lang.Byte.ByteCache[]",
        "java.lang.Byte$ByteCache[]",
        "[Ljava.lang.Byte$ByteCache;",
        "[Ljava/lang/Byte$ByteCache;");
  }

  @SuppressWarnings(
      "UnusedVariable") // no methods that transform fully-qualified names (which are rarely used)
  private static void checkTypeStrings(
      @FullyQualifiedName String fqn,
      @BinaryName String bn,
      @ClassGetName String cgn,
      @FieldDescriptor String fd) {
    assertTrue(fd.equals(Signatures.binaryNameToFieldDescriptor(bn)));
    assert cgn.equals(Signatures.binaryNameToClassGetName(bn))
        : bn + " => " + Signatures.binaryNameToClassGetName(bn) + ", should be " + cgn;
    assertTrue(cgn.equals(Signatures.fieldDescriptorToClassGetName(fd)));
    assertTrue(bn.equals(Signatures.fieldDescriptorToBinaryName(fd)));
  }

  ///////////////////////////////////////////////////////////////////////////
  /// Method signatures, which combine multiple types
  ///

  @Test
  public void testSignatureConversions() {
    // public static String arglistToJvm(String arglist)
    assertTrue(Signatures.arglistToJvm("()").equals("()"));
    assertTrue(Signatures.arglistToJvm("(int)").equals("(I)"));
    assertTrue(Signatures.arglistToJvm("(int, int)").equals("(II)"));
    assertTrue(Signatures.arglistToJvm("(int, long, short)").equals("(IJS)"));
    assert Signatures.arglistToJvm("(java.lang.Integer, int, java.lang.Integer)")
        .equals("(Ljava/lang/Integer;ILjava/lang/Integer;)");
    assertTrue(Signatures.arglistToJvm("(int[])").equals("([I)"));
    assertTrue(Signatures.arglistToJvm("(int[], int, int)").equals("([III)"));
    assertTrue(Signatures.arglistToJvm("(int, int[][], int)").equals("(I[[II)"));
    assert Signatures.arglistToJvm("(java.lang.Integer[], int, java.lang.Integer[][])")
        .equals("([Ljava/lang/Integer;I[[Ljava/lang/Integer;)");

    // public static String arglistFromJvm(String arglist)
    assertTrue(Signatures.arglistFromJvm("()").equals("()"));
    assertTrue(Signatures.arglistFromJvm("(I)").equals("(int)"));
    assertTrue(Signatures.arglistFromJvm("(II)").equals("(int, int)"));
    assertTrue(Signatures.arglistFromJvm("(IJS)").equals("(int, long, short)"));
    assert Signatures.arglistFromJvm("(Ljava/lang/Integer;ILjava/lang/Integer;)")
        .equals("(java.lang.Integer, int, java.lang.Integer)");
    assertTrue(Signatures.arglistFromJvm("([I)").equals("(int[])"));
    assertTrue(Signatures.arglistFromJvm("([III)").equals("(int[], int, int)"));
    assertTrue(Signatures.arglistFromJvm("(I[[II)").equals("(int, int[][], int)"));
    assert Signatures.arglistFromJvm("([Ljava/lang/Integer;I[[Ljava/lang/Integer;)")
        .equals("(java.lang.Integer[], int, java.lang.Integer[][])");
  }
}
