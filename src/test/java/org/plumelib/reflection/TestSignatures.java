package org.plumelib.reflection;

import org.checkerframework.checker.signature.qual.BinaryName;
import org.checkerframework.checker.signature.qual.ClassGetName;
import org.checkerframework.checker.signature.qual.FieldDescriptor;
import org.checkerframework.checker.signature.qual.FullyQualifiedName;
import org.junit.Test;

/** Test code for the Signatures class. */
@SuppressWarnings({
  "interning", // interning is due to apparent bugs
  "UseCorrectAssertInTests" // I don't see the problem with using `assert`
})
public final class TestSignatures {

  // This cannot be static because it instantiates an inner class.
  @SuppressWarnings("ArrayEquals")
  @Test
  public void testSignatures() {

    // public static String binaryNameToFieldDescriptor(String classname)
    assert Signatures.binaryNameToFieldDescriptor("boolean").equals("Z");
    assert Signatures.binaryNameToFieldDescriptor("byte").equals("B");
    assert Signatures.binaryNameToFieldDescriptor("char").equals("C");
    assert Signatures.binaryNameToFieldDescriptor("double").equals("D");
    assert Signatures.binaryNameToFieldDescriptor("float").equals("F");
    assert Signatures.binaryNameToFieldDescriptor("int").equals("I");
    assert Signatures.binaryNameToFieldDescriptor("long").equals("J");
    assert Signatures.binaryNameToFieldDescriptor("short").equals("S");
    assert Signatures.binaryNameToFieldDescriptor("Integer").equals("LInteger;");
    assert Signatures.binaryNameToFieldDescriptor("Java.lang.Integer")
        .equals("LJava/lang/Integer;");
    assert Signatures.binaryNameToFieldDescriptor("int[][]").equals("[[I");
    assert Signatures.binaryNameToFieldDescriptor("Java.lang.Integer[][][]")
        .equals("[[[LJava/lang/Integer;");

    // public static @ClassGetName String binaryNameToClassGetName(/*BinaryName*/ String bn)
    assert Signatures.binaryNameToClassGetName("boolean").equals("boolean");
    assert Signatures.binaryNameToClassGetName("byte").equals("byte");
    assert Signatures.binaryNameToClassGetName("char").equals("char");
    assert Signatures.binaryNameToClassGetName("double").equals("double");
    assert Signatures.binaryNameToClassGetName("float").equals("float");
    assert Signatures.binaryNameToClassGetName("int").equals("int");
    assert Signatures.binaryNameToClassGetName("long").equals("long");
    assert Signatures.binaryNameToClassGetName("short").equals("short");
    assert Signatures.binaryNameToClassGetName("Integer").equals("Integer");
    assert Signatures.binaryNameToClassGetName("Java.lang.Integer").equals("Java.lang.Integer");
    assert Signatures.binaryNameToClassGetName("int[][]").equals("[[I");
    assert Signatures.binaryNameToClassGetName("Java.lang.Integer[][][]")
        .equals("[[[LJava.lang.Integer;");

    // public static String arglistToJvm(String arglist)
    assert Signatures.arglistToJvm("()").equals("()");
    assert Signatures.arglistToJvm("(int)").equals("(I)");
    assert Signatures.arglistToJvm("(int, int)").equals("(II)");
    assert Signatures.arglistToJvm("(int, long, short)").equals("(IJS)");
    assert Signatures.arglistToJvm("(java.lang.Integer, int, java.lang.Integer)")
        .equals("(Ljava/lang/Integer;ILjava/lang/Integer;)");
    assert Signatures.arglistToJvm("(int[])").equals("([I)");
    assert Signatures.arglistToJvm("(int[], int, int)").equals("([III)");
    assert Signatures.arglistToJvm("(int, int[][], int)").equals("(I[[II)");
    assert Signatures.arglistToJvm("(java.lang.Integer[], int, java.lang.Integer[][])")
        .equals("([Ljava/lang/Integer;I[[Ljava/lang/Integer;)");

    // public static String fieldDescriptorToBinaryName(String classname)
    assert Signatures.fieldDescriptorToBinaryName("Z").equals("boolean");
    assert Signatures.fieldDescriptorToBinaryName("B").equals("byte");
    assert Signatures.fieldDescriptorToBinaryName("C").equals("char");
    assert Signatures.fieldDescriptorToBinaryName("D").equals("double");
    assert Signatures.fieldDescriptorToBinaryName("F").equals("float");
    assert Signatures.fieldDescriptorToBinaryName("I").equals("int");
    assert Signatures.fieldDescriptorToBinaryName("J").equals("long");
    assert Signatures.fieldDescriptorToBinaryName("S").equals("short");
    assert Signatures.fieldDescriptorToBinaryName("LInteger;").equals("Integer");
    assert Signatures.fieldDescriptorToBinaryName("LJava/lang/Integer;")
        .equals("Java.lang.Integer");
    assert Signatures.fieldDescriptorToBinaryName("[[I").equals("int[][]");
    assert Signatures.fieldDescriptorToBinaryName("[[LJava/lang/Integer;")
        .equals("Java.lang.Integer[][]");

    // public static @ClassGetName String
    //     fieldDescriptorToClassGetName(/*FieldDescriptor*/ String fd)
    assert Signatures.fieldDescriptorToClassGetName("Z").equals("boolean");
    assert Signatures.fieldDescriptorToClassGetName("B").equals("byte");
    assert Signatures.fieldDescriptorToClassGetName("C").equals("char");
    assert Signatures.fieldDescriptorToClassGetName("D").equals("double");
    assert Signatures.fieldDescriptorToClassGetName("F").equals("float");
    assert Signatures.fieldDescriptorToClassGetName("I").equals("int");
    assert Signatures.fieldDescriptorToClassGetName("J").equals("long");
    assert Signatures.fieldDescriptorToClassGetName("S").equals("short");
    assert Signatures.fieldDescriptorToClassGetName("LInteger;").equals("Integer");
    assert Signatures.fieldDescriptorToClassGetName("LJava/lang/Integer;")
        .equals("Java.lang.Integer");
    assert Signatures.fieldDescriptorToClassGetName("[[I").equals("[[I");
    assert Signatures.fieldDescriptorToClassGetName("[[LJava/lang/Integer;")
        .equals("[[LJava.lang.Integer;");

    // public static String arglistFromJvm(String arglist)
    assert Signatures.arglistFromJvm("()").equals("()");
    assert Signatures.arglistFromJvm("(I)").equals("(int)");
    assert Signatures.arglistFromJvm("(II)").equals("(int, int)");
    assert Signatures.arglistFromJvm("(IJS)").equals("(int, long, short)");
    assert Signatures.arglistFromJvm("(Ljava/lang/Integer;ILjava/lang/Integer;)")
        .equals("(java.lang.Integer, int, java.lang.Integer)");
    assert Signatures.arglistFromJvm("([I)").equals("(int[])");
    assert Signatures.arglistFromJvm("([III)").equals("(int[], int, int)");
    assert Signatures.arglistFromJvm("(I[[II)").equals("(int, int[][], int)");
    assert Signatures.arglistFromJvm("([Ljava/lang/Integer;I[[Ljava/lang/Integer;)")
        .equals("(java.lang.Integer[], int, java.lang.Integer[][])");

    // More tests for type representation conversions.
    // Table from Signature Checker manual.
    checkTypeStrings("int", "int", "int", "I");
    checkTypeStrings("int[][]", "int[][]", "[[I", "[[I");
    checkTypeStrings("MyClass", "MyClass", "MyClass", "LMyClass;");
    checkTypeStrings("MyClass[]", "MyClass[]", "[LMyClass;", "[LMyClass;");
    checkTypeStrings(
        "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "Ljava/lang/Integer;");
    checkTypeStrings(
        "java.lang.Integer[]",
        "java.lang.Integer[]",
        "[Ljava.lang.Integer;",
        "[Ljava/lang/Integer;");
    checkTypeStrings(
        "java.lang.Byte.ByteCache",
        "java.lang.Byte$ByteCache",
        "java.lang.Byte$ByteCache",
        "Ljava/lang/Byte$ByteCache;");
    checkTypeStrings(
        "java.lang.Byte.ByteCache[]",
        "java.lang.Byte$ByteCache[]",
        "[Ljava.lang.Byte$ByteCache;",
        "[Ljava/lang/Byte$ByteCache;");
  }

  private static void checkTypeStrings(
      @FullyQualifiedName String fqn,
      @BinaryName String bn,
      @ClassGetName String cgn,
      @FieldDescriptor String fd) {
    assert fd.equals(Signatures.binaryNameToFieldDescriptor(bn));
    assert cgn.equals(Signatures.binaryNameToClassGetName(bn))
        : bn + " => " + Signatures.binaryNameToClassGetName(bn) + ", should be " + cgn;
    assert cgn.equals(Signatures.fieldDescriptorToClassGetName(fd)) : fd + " => " + cgn;
    assert bn.equals(Signatures.fieldDescriptorToBinaryName(fd));
  }
}
