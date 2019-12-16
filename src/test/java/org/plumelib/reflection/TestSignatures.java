package org.plumelib.reflection;

import static org.junit.Assert.assertEquals;
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
    assertEquals("int", Signatures.getArrayElementType("int[][][]"));
    assertEquals("int", Signatures.getArrayElementType("int"));
  }

  /** Given a filename ending with ".class", return the class name. */
  @Test
  public void testClassfilenameToBinaryName() {
    assertEquals("Quux", Signatures.classfilenameToBinaryName("/foo/bar/baz/Quux.class"));
    assertEquals("Quux", Signatures.classfilenameToBinaryName("Quux.class"));
    assertEquals("Quux$22", Signatures.classfilenameToBinaryName("/foo/bar/baz/Quux$22.class"));
    assertEquals("Quux$22", Signatures.classfilenameToBinaryName("Quux$22.class"));
  }

  ///////////////////////////////////////////////////////////////////////////
  /// String concatenations
  ///

  /*
   * Given a package name and a class name, combine them to form a qualified class name.
   */
  @Test
  public void testAddPackage() {
    assertEquals("Foo", Signatures.addPackage(null, "Foo"));
    assertEquals("a.b.c.Foo", Signatures.addPackage("a.b.c", "Foo"));
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
    assertEquals("Z", Signatures.binaryNameToFieldDescriptor("boolean"));
    assertEquals("B", Signatures.binaryNameToFieldDescriptor("byte"));
    assertEquals("C", Signatures.binaryNameToFieldDescriptor("char"));
    assertEquals("D", Signatures.binaryNameToFieldDescriptor("double"));
    assertEquals("F", Signatures.binaryNameToFieldDescriptor("float"));
    assertEquals("I", Signatures.binaryNameToFieldDescriptor("int"));
    assertEquals("J", Signatures.binaryNameToFieldDescriptor("long"));
    assertEquals("S", Signatures.binaryNameToFieldDescriptor("short"));
    assertEquals("LInteger;", Signatures.binaryNameToFieldDescriptor("Integer"));
    assertEquals(
        "LJava/lang/Integer;", Signatures.binaryNameToFieldDescriptor("Java.lang.Integer"));
    assertEquals("[[I", Signatures.binaryNameToFieldDescriptor("int[][]"));
    assertEquals(
        "[Ljava/lang/Object;", Signatures.binaryNameToFieldDescriptor("java.lang.Object[]"));

    // public static @ClassGetName String binaryNameToClassGetName(/*BinaryName*/ String bn)
    assertEquals("boolean", Signatures.binaryNameToClassGetName("boolean"));
    assertEquals("byte", Signatures.binaryNameToClassGetName("byte"));
    assertEquals("char", Signatures.binaryNameToClassGetName("char"));
    assertEquals("double", Signatures.binaryNameToClassGetName("double"));
    assertEquals("float", Signatures.binaryNameToClassGetName("float"));
    assertEquals("int", Signatures.binaryNameToClassGetName("int"));
    assertEquals("long", Signatures.binaryNameToClassGetName("long"));
    assertEquals("short", Signatures.binaryNameToClassGetName("short"));
    assertEquals("Integer", Signatures.binaryNameToClassGetName("Integer"));
    assertEquals("Java.lang.Integer", Signatures.binaryNameToClassGetName("Java.lang.Integer"));
    @SuppressWarnings("signature:assignment.type.incompatible") // test beyond the method's contract
    @BinaryName String intArrayAray = "int[][]", objectArray = "java.lang.Object[]";
    assertEquals("[[I", Signatures.binaryNameToClassGetName(intArrayAray));
    assertEquals("[Ljava.lang.Object;", Signatures.binaryNameToClassGetName(objectArray));

    // public static String fieldDescriptorToBinaryName(String classname)
    assertEquals("boolean", Signatures.fieldDescriptorToBinaryName("Z"));
    assertEquals("byte", Signatures.fieldDescriptorToBinaryName("B"));
    assertEquals("char", Signatures.fieldDescriptorToBinaryName("C"));
    assertEquals("double", Signatures.fieldDescriptorToBinaryName("D"));
    assertEquals("float", Signatures.fieldDescriptorToBinaryName("F"));
    assertEquals("int", Signatures.fieldDescriptorToBinaryName("I"));
    assertEquals("long", Signatures.fieldDescriptorToBinaryName("J"));
    assertEquals("short", Signatures.fieldDescriptorToBinaryName("S"));
    assertEquals("Integer", Signatures.fieldDescriptorToBinaryName("LInteger;"));
    assertEquals(
        "Java.lang.Integer", Signatures.fieldDescriptorToBinaryName("LJava/lang/Integer;"));
    assertEquals("int[][]", Signatures.fieldDescriptorToBinaryName("[[I"));
    assertEquals(
        "Java.lang.Integer[][]", Signatures.fieldDescriptorToBinaryName("[[LJava/lang/Integer;"));

    // public static @ClassGetName String
    //     fieldDescriptorToClassGetName(/*FieldDescriptor*/ String fd)
    assertEquals("boolean", Signatures.fieldDescriptorToClassGetName("Z"));
    assertEquals("byte", Signatures.fieldDescriptorToClassGetName("B"));
    assertEquals("char", Signatures.fieldDescriptorToClassGetName("C"));
    assertEquals("double", Signatures.fieldDescriptorToClassGetName("D"));
    assertEquals("float", Signatures.fieldDescriptorToClassGetName("F"));
    assertEquals("int", Signatures.fieldDescriptorToClassGetName("I"));
    assertEquals("long", Signatures.fieldDescriptorToClassGetName("J"));
    assertEquals("short", Signatures.fieldDescriptorToClassGetName("S"));
    assertEquals("Integer", Signatures.fieldDescriptorToClassGetName("LInteger;"));
    assertEquals(
        "Java.lang.Integer", Signatures.fieldDescriptorToClassGetName("LJava/lang/Integer;"));
    assertEquals("[[I", Signatures.fieldDescriptorToClassGetName("[[I"));
    assertEquals(
        "[[LJava.lang.Integer;", Signatures.fieldDescriptorToClassGetName("[[LJava/lang/Integer;"));

    assertEquals("MyClass", Signatures.internalFormToClassGetName("MyClass"));
    assertEquals("MyClass$22", Signatures.internalFormToClassGetName("MyClass$22"));
    assertEquals("java.lang.Integer", Signatures.internalFormToClassGetName("java/lang/Integer"));
    assertEquals("pkg.Outer$Inner", Signatures.internalFormToClassGetName("pkg/Outer$Inner"));
    assertEquals("pkg.Outer$22", Signatures.internalFormToClassGetName("pkg/Outer$22"));

    assertEquals("MyClass", Signatures.internalFormToBinaryName("MyClass"));
    assertEquals("MyClass$22", Signatures.internalFormToBinaryName("MyClass$22"));
    assertEquals("java.lang.Integer", Signatures.internalFormToBinaryName("java/lang/Integer"));
    assertEquals("pkg.Outer$Inner", Signatures.internalFormToBinaryName("pkg/Outer$Inner"));
    assertEquals("pkg.Outer$22", Signatures.internalFormToBinaryName("pkg/Outer$22"));

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
    assertEquals("[[I", Signatures.binaryNameToFieldDescriptor("int[][]"));
    assertEquals(
        "[[[LJava/lang/Integer;",
        Signatures.binaryNameToFieldDescriptor("Java.lang.Integer[][][]"));

    // public static @ClassGetName String binaryNameToClassGetName(/*BinaryName*/ String bn)
    assertEquals("[[I", Signatures.binaryNameToClassGetName("int[][]"));
    assertEquals(
        "[[[LJava.lang.Integer;", Signatures.binaryNameToClassGetName("Java.lang.Integer[][][]"));

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
    assertEquals(fd, Signatures.binaryNameToFieldDescriptor(bn));
    assertEquals(cgn, Signatures.binaryNameToClassGetName(bn));
    assertEquals(cgn, Signatures.fieldDescriptorToClassGetName(fd));
    assertEquals(bn, Signatures.fieldDescriptorToBinaryName(fd));
  }

  ///////////////////////////////////////////////////////////////////////////
  /// Method signatures, which combine multiple types
  ///

  @Test
  public void testSignatureConversions() {
    // public static String arglistToJvm(String arglist)
    assertEquals("()", Signatures.arglistToJvm("()"));
    assertEquals("(I)", Signatures.arglistToJvm("(int)"));
    assertEquals("(II)", Signatures.arglistToJvm("(int, int)"));
    assertEquals("(IJS)", Signatures.arglistToJvm("(int, long, short)"));
    assertEquals(
        "(Ljava/lang/Integer;ILjava/lang/Integer;)",
        Signatures.arglistToJvm("(java.lang.Integer, int, java.lang.Integer)"));
    assertEquals("([I)", Signatures.arglistToJvm("(int[])"));
    assertEquals("([III)", Signatures.arglistToJvm("(int[], int, int)"));
    assertEquals("(I[[II)", Signatures.arglistToJvm("(int, int[][], int)"));
    assertEquals(
        "([Ljava/lang/Integer;I[[Ljava/lang/Integer;)",
        Signatures.arglistToJvm("(java.lang.Integer[], int, java.lang.Integer[][])"));

    // public static String arglistFromJvm(String arglist)
    assertEquals("()", Signatures.arglistFromJvm("()"));
    assertEquals("(int)", Signatures.arglistFromJvm("(I)"));
    assertEquals("(int, int)", Signatures.arglistFromJvm("(II)"));
    assertEquals("(int, long, short)", Signatures.arglistFromJvm("(IJS)"));
    assertEquals(
        "(java.lang.Integer, int, java.lang.Integer)",
        Signatures.arglistFromJvm("(Ljava/lang/Integer;ILjava/lang/Integer;)"));
    assertEquals("(int[])", Signatures.arglistFromJvm("([I)"));
    assertEquals("(int[], int, int)", Signatures.arglistFromJvm("([III)"));
    assertEquals("(int, int[][], int)", Signatures.arglistFromJvm("(I[[II)"));
    assertEquals(
        "(java.lang.Integer[], int, java.lang.Integer[][])",
        Signatures.arglistFromJvm("([Ljava/lang/Integer;I[[Ljava/lang/Integer;)"));
  }
}
