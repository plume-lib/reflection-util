package org.plumelib.signature;

import org.checkerframework.checker.signature.qual.BinaryName;
import org.checkerframework.checker.signature.qual.ClassGetName;
import org.checkerframework.checker.signature.qual.FieldDescriptor;
import org.checkerframework.checker.signature.qual.FullyQualifiedName;
import org.junit.Test;

/*>>>
import org.checkerframework.checker.index.qual.*;
import org.checkerframework.checker.lock.qual.*;
import org.checkerframework.checker.nullness.qual.*;
import org.checkerframework.checker.signature.qual.*;
import org.checkerframework.common.value.qual.*;
*/

/** Test code for the bcel-util package. */
@SuppressWarnings({
  "interning", // interning is due to apparent bugs
  "UseCorrectAssertInTests" // I don't see the problem with using `assert`
})
public final class TestSignatureUtil {

  // This cannot be static because it instantiates an inner class.
  @SuppressWarnings("ArrayEquals")
  @Test
  public void testSignatureUtil() {

    // public static String binaryNameToFieldDescriptor(String classname)
    assert SignatureUtil.binaryNameToFieldDescriptor("boolean").equals("Z");
    assert SignatureUtil.binaryNameToFieldDescriptor("byte").equals("B");
    assert SignatureUtil.binaryNameToFieldDescriptor("char").equals("C");
    assert SignatureUtil.binaryNameToFieldDescriptor("double").equals("D");
    assert SignatureUtil.binaryNameToFieldDescriptor("float").equals("F");
    assert SignatureUtil.binaryNameToFieldDescriptor("int").equals("I");
    assert SignatureUtil.binaryNameToFieldDescriptor("long").equals("J");
    assert SignatureUtil.binaryNameToFieldDescriptor("short").equals("S");
    assert SignatureUtil.binaryNameToFieldDescriptor("Integer").equals("LInteger;");
    assert SignatureUtil.binaryNameToFieldDescriptor("Java.lang.Integer")
        .equals("LJava/lang/Integer;");
    assert SignatureUtil.binaryNameToFieldDescriptor("int[][]").equals("[[I");
    assert SignatureUtil.binaryNameToFieldDescriptor("Java.lang.Integer[][][]")
        .equals("[[[LJava/lang/Integer;");

    // public static @ClassGetName String binaryNameToClassGetName(/*BinaryName*/ String bn)
    assert SignatureUtil.binaryNameToClassGetName("boolean").equals("boolean");
    assert SignatureUtil.binaryNameToClassGetName("byte").equals("byte");
    assert SignatureUtil.binaryNameToClassGetName("char").equals("char");
    assert SignatureUtil.binaryNameToClassGetName("double").equals("double");
    assert SignatureUtil.binaryNameToClassGetName("float").equals("float");
    assert SignatureUtil.binaryNameToClassGetName("int").equals("int");
    assert SignatureUtil.binaryNameToClassGetName("long").equals("long");
    assert SignatureUtil.binaryNameToClassGetName("short").equals("short");
    assert SignatureUtil.binaryNameToClassGetName("Integer").equals("Integer");
    assert SignatureUtil.binaryNameToClassGetName("Java.lang.Integer").equals("Java.lang.Integer");
    assert SignatureUtil.binaryNameToClassGetName("int[][]").equals("[[I");
    assert SignatureUtil.binaryNameToClassGetName("Java.lang.Integer[][][]")
        .equals("[[[LJava.lang.Integer;");

    // public static String arglistToJvm(String arglist)
    assert SignatureUtil.arglistToJvm("()").equals("()");
    assert SignatureUtil.arglistToJvm("(int)").equals("(I)");
    assert SignatureUtil.arglistToJvm("(int, int)").equals("(II)");
    assert SignatureUtil.arglistToJvm("(int, long, short)").equals("(IJS)");
    assert SignatureUtil.arglistToJvm("(java.lang.Integer, int, java.lang.Integer)")
        .equals("(Ljava/lang/Integer;ILjava/lang/Integer;)");
    assert SignatureUtil.arglistToJvm("(int[])").equals("([I)");
    assert SignatureUtil.arglistToJvm("(int[], int, int)").equals("([III)");
    assert SignatureUtil.arglistToJvm("(int, int[][], int)").equals("(I[[II)");
    assert SignatureUtil.arglistToJvm("(java.lang.Integer[], int, java.lang.Integer[][])")
        .equals("([Ljava/lang/Integer;I[[Ljava/lang/Integer;)");

    // public static String fieldDescriptorToBinaryName(String classname)
    assert SignatureUtil.fieldDescriptorToBinaryName("Z").equals("boolean");
    assert SignatureUtil.fieldDescriptorToBinaryName("B").equals("byte");
    assert SignatureUtil.fieldDescriptorToBinaryName("C").equals("char");
    assert SignatureUtil.fieldDescriptorToBinaryName("D").equals("double");
    assert SignatureUtil.fieldDescriptorToBinaryName("F").equals("float");
    assert SignatureUtil.fieldDescriptorToBinaryName("I").equals("int");
    assert SignatureUtil.fieldDescriptorToBinaryName("J").equals("long");
    assert SignatureUtil.fieldDescriptorToBinaryName("S").equals("short");
    assert SignatureUtil.fieldDescriptorToBinaryName("LInteger;").equals("Integer");
    assert SignatureUtil.fieldDescriptorToBinaryName("LJava/lang/Integer;")
        .equals("Java.lang.Integer");
    assert SignatureUtil.fieldDescriptorToBinaryName("[[I").equals("int[][]");
    assert SignatureUtil.fieldDescriptorToBinaryName("[[LJava/lang/Integer;")
        .equals("Java.lang.Integer[][]");

    // public static @ClassGetName String
    //     fieldDescriptorToClassGetName(/*FieldDescriptor*/ String fd)
    assert SignatureUtil.fieldDescriptorToClassGetName("Z").equals("boolean");
    assert SignatureUtil.fieldDescriptorToClassGetName("B").equals("byte");
    assert SignatureUtil.fieldDescriptorToClassGetName("C").equals("char");
    assert SignatureUtil.fieldDescriptorToClassGetName("D").equals("double");
    assert SignatureUtil.fieldDescriptorToClassGetName("F").equals("float");
    assert SignatureUtil.fieldDescriptorToClassGetName("I").equals("int");
    assert SignatureUtil.fieldDescriptorToClassGetName("J").equals("long");
    assert SignatureUtil.fieldDescriptorToClassGetName("S").equals("short");
    assert SignatureUtil.fieldDescriptorToClassGetName("LInteger;").equals("Integer");
    assert SignatureUtil.fieldDescriptorToClassGetName("LJava/lang/Integer;")
        .equals("Java.lang.Integer");
    assert SignatureUtil.fieldDescriptorToClassGetName("[[I").equals("[[I");
    assert SignatureUtil.fieldDescriptorToClassGetName("[[LJava/lang/Integer;")
        .equals("[[LJava.lang.Integer;");

    // public static String arglistFromJvm(String arglist)
    assert SignatureUtil.arglistFromJvm("()").equals("()");
    assert SignatureUtil.arglistFromJvm("(I)").equals("(int)");
    assert SignatureUtil.arglistFromJvm("(II)").equals("(int, int)");
    assert SignatureUtil.arglistFromJvm("(IJS)").equals("(int, long, short)");
    assert SignatureUtil.arglistFromJvm("(Ljava/lang/Integer;ILjava/lang/Integer;)")
        .equals("(java.lang.Integer, int, java.lang.Integer)");
    assert SignatureUtil.arglistFromJvm("([I)").equals("(int[])");
    assert SignatureUtil.arglistFromJvm("([III)").equals("(int[], int, int)");
    assert SignatureUtil.arglistFromJvm("(I[[II)").equals("(int, int[][], int)");
    assert SignatureUtil.arglistFromJvm("([Ljava/lang/Integer;I[[Ljava/lang/Integer;)")
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
    assert fd.equals(SignatureUtil.binaryNameToFieldDescriptor(bn));
    assert cgn.equals(SignatureUtil.binaryNameToClassGetName(bn))
        : bn + " => " + SignatureUtil.binaryNameToClassGetName(bn) + ", should be " + cgn;
    assert cgn.equals(SignatureUtil.fieldDescriptorToClassGetName(fd)) : fd + " => " + cgn;
    assert bn.equals(SignatureUtil.fieldDescriptorToBinaryName(fd));
  }
}
