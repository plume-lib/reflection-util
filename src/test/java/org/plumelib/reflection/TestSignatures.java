package org.plumelib.reflection;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signature.qual.BinaryName;
import org.checkerframework.checker.signature.qual.ClassGetName;
import org.checkerframework.checker.signature.qual.ClassGetSimpleName;
import org.checkerframework.checker.signature.qual.FieldDescriptor;
import org.checkerframework.checker.signature.qual.FqBinaryName;
import org.checkerframework.checker.signature.qual.FullyQualifiedName;
import org.checkerframework.checker.signature.qual.InternalForm;
import org.junit.jupiter.api.Test;

/** Test code for the Signatures class. */
public final class TestSignatures {

  // //////////////////////////////////////////////////////////////////////
  // Accessing parts of types
  //

  /**
   * Returns the element type for the given type name, which results from removing all the array
   * brackets.
   */
  @Test
  void test_getArrayElementType() {
    assertEquals("int", Signatures.getArrayElementType("int[][][]"));
    assertEquals("int", Signatures.getArrayElementType("int"));
  }

  /** Given a filename ending with ".class", return the binary name of the class. */
  @Test
  void test_classfilenameToBinaryName() {
    assertEquals(
        "foo.bar.baz.Quux", Signatures.classfilenameToBinaryName("/foo/bar/baz/Quux.class"));
    assertEquals(
        "foo.bar.baz.Quux", Signatures.classfilenameToBinaryName("foo/bar/baz/Quux.class"));
    assertEquals("Quux", Signatures.classfilenameToBinaryName("Quux.class"));
    assertEquals(
        "foo.bar.baz.Quux$22", Signatures.classfilenameToBinaryName("/foo/bar/baz/Quux$22.class"));
    assertEquals(
        "foo.bar.baz.Quux$22", Signatures.classfilenameToBinaryName("foo/bar/baz/Quux$22.class"));
    assertEquals("Quux$22", Signatures.classfilenameToBinaryName("Quux$22.class"));
  }

  /** Given a filename ending with ".class", return the simple binary name of the class. */
  @Test
  void test_classfilenameToBaseName() {
    assertEquals("Quux", Signatures.classfilenameToBaseName("/foo/bar/baz/Quux.class"));
    assertEquals("Quux", Signatures.classfilenameToBaseName("foo/bar/baz/Quux.class"));
    assertEquals("Quux", Signatures.classfilenameToBaseName("Quux.class"));
    assertEquals("Quux$22", Signatures.classfilenameToBaseName("/foo/bar/baz/Quux$22.class"));
    assertEquals("Quux$22", Signatures.classfilenameToBaseName("foo/bar/baz/Quux$22.class"));
    assertEquals("Quux$22", Signatures.classfilenameToBaseName("Quux$22.class"));
  }

  // //////////////////////////////////////////////////////////////////////
  // String concatenations
  //

  /*
   * Given a package name and a class name, combine them to form a qualified class name.
   */
  @Test
  void test_addPackage() {
    assertEquals("Foo", Signatures.addPackage(null, "Foo"));
    assertEquals("a.b.c.Foo", Signatures.addPackage("a.b.c", "Foo"));
  }

  // //////////////////////////////////////////////////////////////////////
  // Type tests
  //

  /**
   * Returns true if the argument has the format of a BinaryName. The type it refers to might or
   * might not exist.
   */
  @Test
  void test_isBinaryName() {
    assertTrue(!Signatures.isBinaryName("int"));
    assertTrue(!Signatures.isBinaryName("int[][]"));
    assertTrue(Signatures.isBinaryName("java.lang.String"));
    assertTrue(!Signatures.isBinaryName("java.lang.String[]"));
    assertTrue(Signatures.isBinaryName("MyClass$22"));
    assertTrue(!Signatures.isBinaryName("MyClass$22[]"));
    assertTrue(Signatures.isBinaryName("pkg.Outer$Inner"));
    assertTrue(!Signatures.isBinaryName("pkg.Outer$Inner[]"));
    assertTrue(Signatures.isBinaryName("Class$Inner._"));
    assertTrue(Signatures.isBinaryName("com.google.gson.internal.$Gson$Types"));
  }

  /**
   * Returns true if the argument has the format of a ClassGetName. The type it refers to might or
   * might not exist.
   */
  @Test
  void test_isClassGetName() {
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
    assertTrue(Signatures.isClassGetName("com.google.gson.internal.$Gson$Types"));
  }

  /**
   * Returns true if the argument has the format of a DotSeparatedIdentifiers. The package or type
   * it refers to might or might not exist.
   */
  @Test
  void test_isDotSeparatedIdentifiers() {
    assertTrue(!Signatures.isDotSeparatedIdentifiers("hello world"));
    assertTrue(!Signatures.isDotSeparatedIdentifiers("[[I"));
    assertTrue(!Signatures.isDotSeparatedIdentifiers("int"));
    assertTrue(!Signatures.isDotSeparatedIdentifiers("int[][]"));
    assertTrue(Signatures.isDotSeparatedIdentifiers("java.lang.String"));
    assertTrue(!Signatures.isDotSeparatedIdentifiers("java.lang.String[]"));
    assertTrue(Signatures.isDotSeparatedIdentifiers("MyClass$22"));
    assertTrue(!Signatures.isDotSeparatedIdentifiers("MyClass$22[]"));
    assertTrue(Signatures.isDotSeparatedIdentifiers("pkg.Outer$Inner"));
    assertTrue(!Signatures.isDotSeparatedIdentifiers("pkg.Outer$Inner[]"));
    assertTrue(Signatures.isDotSeparatedIdentifiers("Class$Inner._"));
  }

  /**
   * Returns true if the argument has the format of a FqBinaryName. The type it refers to might or
   * might not exist.
   */
  @Test
  void test_isFqBinaryName() {
    assertTrue(!Signatures.isFqBinaryName("hello world"));
    assertTrue(!Signatures.isFqBinaryName("[[I"));
    assertTrue(Signatures.isFqBinaryName("int"));
    assertTrue(Signatures.isFqBinaryName("int[]"));
    assertTrue(Signatures.isFqBinaryName("int[][]"));
    assertTrue(Signatures.isFqBinaryName("java.lang.String"));
    assertTrue(Signatures.isFqBinaryName("java.lang.String[]"));
    assertTrue(Signatures.isFqBinaryName("java.lang.String[][]"));
    assertTrue(Signatures.isFqBinaryName("MyClass$22"));
    assertTrue(Signatures.isFqBinaryName("MyClass$22[]"));
    assertTrue(Signatures.isFqBinaryName("MyClass$22[][]"));
    assertTrue(Signatures.isFqBinaryName("pkg.Outer$Inner"));
    assertTrue(Signatures.isFqBinaryName("pkg.Outer$Inner[]"));
    assertTrue(Signatures.isFqBinaryName("pkg.Outer$Inner[][]"));
  }

  /**
   * Returns true if the argument has the format of a FqBinaryName. The type it refers to might or
   * might not exist.
   */
  @Test
  void test_isIdentifier() {
    assertTrue(Signatures.isIdentifier("_"));
    assertTrue(Signatures.isIdentifier("Class$Inner"));
  }

  // //////////////////////////////////////////////////////////////////////
  // Type conversions
  //

  private void assertParseFqBinaryName(
      @FqBinaryName String typename, String classname, int dimensions) {
    Signatures.ClassnameAndDimensions cad =
        Signatures.ClassnameAndDimensions.parseFqBinaryName(typename);
    assertEquals(classname, cad.classname);
    assertEquals(dimensions, cad.dimensions);
  }

  @Test
  void test_parseFqBinaryName() {
    assertParseFqBinaryName("int", "int", 0);
    assertParseFqBinaryName("int[]", "int", 1);
    assertParseFqBinaryName("int[][]", "int", 2);
    assertParseFqBinaryName("java.lang.String", "java.lang.String", 0);
    assertParseFqBinaryName("java.lang.String[]", "java.lang.String", 1);
    assertParseFqBinaryName("java.lang.String[][]", "java.lang.String", 2);
    assertParseFqBinaryName("MyClass$22", "MyClass$22", 0);
    assertParseFqBinaryName("MyClass$22[]", "MyClass$22", 1);
    assertParseFqBinaryName("MyClass$22[][]", "MyClass$22", 2);
    assertParseFqBinaryName("pkg.Outer$Inner", "pkg.Outer$Inner", 0);
    assertParseFqBinaryName("pkg.Outer$Inner[]", "pkg.Outer$Inner", 1);
    assertParseFqBinaryName("pkg.Outer$Inner[][]", "pkg.Outer$Inner", 2);
  }

  @Test
  void test_conversions() {
    // Table from Checker Framework manual.  Order of arguments:
    // @FqBinaryName String fqbn,
    // @Nullable @FullyQualifiedName String fqn,
    // @ClassGetName String cgn,
    // @FieldDescriptor String fd,
    // @Nullable @BinaryName String bn,
    // @Nullable @InternalForm String iform,
    // @Nullable @ClassGetSimpleName String cgsn) {

    checkTypeConversions("int", "int", "int", "I", null, null, "int");
    checkTypeConversions("int[][]", "int[][]", "[[I", "[[I", null, null, "int[][]");
    checkTypeConversions(
        "MyClass", "MyClass", "MyClass", "LMyClass;", "MyClass", "MyClass", "MyClass");
    checkTypeConversions(
        "MyClass[]", "MyClass[]", "[LMyClass;", "[LMyClass;", null, null, "MyClass[]");
    checkTypeConversions(
        "MyClass$22", null, "MyClass$22", "LMyClass$22;", "MyClass$22", "MyClass$22", "");
    checkTypeConversions("MyClass$22[]", null, "[LMyClass$22;", "[LMyClass$22;", null, null, "[]");
    checkTypeConversions(
        "java.lang.Integer",
        "java.lang.Integer",
        "java.lang.Integer",
        "Ljava/lang/Integer;",
        "java.lang.Integer",
        "java/lang/Integer",
        "Integer");
    checkTypeConversions(
        "java.lang.Integer[]",
        "java.lang.Integer[]",
        "[Ljava.lang.Integer;",
        "[Ljava/lang/Integer;",
        null,
        null,
        "Integer[]");
    checkTypeConversions(
        "pkg.Outer$Inner",
        "pkg.Outer.Inner",
        "pkg.Outer$Inner",
        "Lpkg/Outer$Inner;",
        "pkg.Outer$Inner",
        "pkg/Outer$Inner",
        "Inner");
    checkTypeConversions(
        "pkg.Outer$Inner[]",
        "pkg.Outer.Inner[]",
        "[Lpkg.Outer$Inner;",
        "[Lpkg/Outer$Inner;",
        null,
        null,
        "Inner[]");
    checkTypeConversions(
        "pkg.Outer$22", null, "pkg.Outer$22", "Lpkg/Outer$22;", "pkg.Outer$22", "pkg/Outer$22", "");
    checkTypeConversions(
        "pkg.Outer$22[]", null, "[Lpkg.Outer$22;", "[Lpkg/Outer$22;", null, null, "[]");

    // All primitive types
    checkTypeConversions("boolean", "boolean", "boolean", "Z", null, null, "boolean");
    checkTypeConversions("byte", "byte", "byte", "B", null, null, "byte");
    checkTypeConversions("char", "char", "char", "C", null, null, "char");
    checkTypeConversions("double", "double", "double", "D", null, null, "double");
    checkTypeConversions("float", "float", "float", "F", null, null, "float");
    checkTypeConversions("int", "int", "int", "I", null, null, "int");
    checkTypeConversions("long", "long", "long", "J", null, null, "long");
    checkTypeConversions("short", "short", "short", "S", null, null, "short");
  }

  private static void checkTypeConversions(
      @FqBinaryName String fqbn,
      @Nullable @FullyQualifiedName String fqn,
      @ClassGetName String cgn,
      @FieldDescriptor String fd,
      @Nullable @BinaryName String bn,
      @Nullable @InternalForm String iform,
      @Nullable @ClassGetSimpleName String cgsn) {

    assertEquals(fd, Signatures.binaryNameToFieldDescriptor(fqbn));
    // assertEquals(fd, Signatures.primitiveTypeNameToFieldDescriptor(String primitiveName));
    if (bn != null) {
      assertEquals(cgn, Signatures.binaryNameToClassGetName(bn));
    }
    if (fqn != null && bn != null) {
      assertEquals(fqn, Signatures.binaryNameToFullyQualified(bn));
    }
    assertEquals(cgn, Signatures.fieldDescriptorToClassGetName(fd));
    if (bn != null) {
      assertEquals(bn, Signatures.fieldDescriptorToBinaryName(fd));
      assertEquals(bn, Signatures.classGetNameToBinaryName(cgn));
    }
    if (fqn != null) {
      assertEquals(fqn, Signatures.fieldDescriptorToFullyQualified(fd));
    }
    if (iform != null) {
      assertEquals(cgn, Signatures.internalFormToClassGetName(iform));
    }
    if (bn != null && iform != null) {
      assertEquals(bn, Signatures.internalFormToBinaryName(iform));
    }
    if (fqn != null && iform != null) {
      assertEquals(fqn, Signatures.internalFormToFullyQualified(iform));
    }
    if (cgsn != null && fqn != null) {
      assertEquals(cgsn, ReflectionPlume.fullyQualifiedNameToSimpleName(fqn));
    }
  }

  // //////////////////////////////////////////////////////////////////////
  // Method signatures, which combine multiple types
  //

  @Test
  void test_signatureSplitting() {
    assertArrayEquals(new String[0], Signatures.splitJavaArglist("()"));
    assertArrayEquals(new String[] {"int"}, Signatures.splitJavaArglist("(int)"));

    assertEquals(Collections.emptyList(), Signatures.splitJvmArglist("()"));
    assertEquals(Collections.singletonList("I"), Signatures.splitJvmArglist("(I)"));
  }

  @Test
  void test_signatureConversions() {
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
