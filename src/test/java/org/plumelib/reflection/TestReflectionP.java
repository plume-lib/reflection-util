package org.plumelib.reflection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

/** Test code for the ReflectionP class. */
public final class TestReflectionP {

  static class Inner {
    static class InnerInner {}
  }

  @Test
  void test_classForName() {
    try {
      assertTrue(ReflectionP.classForName("int") == int.class);
      assertTrue(ReflectionP.classForName("boolean") == boolean.class);
      assertTrue(ReflectionP.classForName("java.lang.Class") == Class.class);
      assertTrue(ReflectionP.classForName("java.util.Map.Entry") == java.util.Map.Entry.class);
      assertTrue(ReflectionP.classForName("java.util.Map$Entry") == java.util.Map.Entry.class);
      assert ReflectionP.classForName("org.plumelib.reflection.TestReflectionP.Inner.InnerInner")
          == Inner.InnerInner.class;
      assert ReflectionP.classForName("org.plumelib.reflection.TestReflectionP.Inner$InnerInner")
          == Inner.InnerInner.class;
      boolean success =
          ReflectionP.classForName("org.plumelib.reflection.TestReflectionP$Inner.InnerInner")
              == Inner.InnerInner.class;
      assertTrue(success);
      assert ReflectionP.classForName("org.plumelib.reflection.TestReflectionP$Inner$InnerInner")
          == Inner.InnerInner.class;
    } catch (ClassNotFoundException e) {
      throw new Error(e);
    }
  }

  @Test
  void test_fullyQualifiedNameToSimpleName() {
    assertEquals("String", ReflectionP.fullyQualifiedNameToSimpleName("java.lang.String"));
    assertEquals("String", ReflectionP.fullyQualifiedNameToSimpleName("String"));
  }

  @Test
  void test_nameWithoutPackage() {
    assertEquals("String", ReflectionP.nameWithoutPackage(String.class));
    assertEquals("Map.Entry", ReflectionP.nameWithoutPackage(java.util.Map.Entry.class));
    assertEquals("TestReflectionP.Inner", ReflectionP.nameWithoutPackage(Inner.class));
    assertEquals(
        "TestReflectionP.Inner.InnerInner", ReflectionP.nameWithoutPackage(Inner.InnerInner.class));
  }

  @Test
  void test_isSubtype() {
    // private boolean isSubtype(Class<?> sub, Class<?> sup) {
    assertTrue(ReflectionP.isSubtype(Integer.class, Integer.class));
    assertTrue(ReflectionP.isSubtype(Cloneable.class, Cloneable.class));
    assertTrue(ReflectionP.isSubtype(Object.class, Object.class));
    assertTrue(ReflectionP.isSubtype(Integer.class, Number.class));
    assertTrue(!ReflectionP.isSubtype(Number.class, Integer.class));
    assertTrue(ReflectionP.isSubtype(Integer.class, Comparable.class));
    assertTrue(!ReflectionP.isSubtype(Comparable.class, Integer.class));
    assertTrue(ReflectionP.isSubtype(Integer.class, Object.class));
    assertTrue(!ReflectionP.isSubtype(Object.class, Integer.class));
    assertTrue(!ReflectionP.isSubtype(Integer.class, Float.class));
    assertTrue(ReflectionP.isSubtype(Collection.class, Iterable.class));
    assertTrue(!ReflectionP.isSubtype(Iterable.class, Collection.class));
    assertTrue(ReflectionP.isSubtype(ArrayList.class, Iterable.class));
    assertTrue(!ReflectionP.isSubtype(Iterable.class, ArrayList.class));
    assertTrue(ReflectionP.isSubtype(ArrayList.class, Cloneable.class));
    assertTrue(!ReflectionP.isSubtype(Cloneable.class, ArrayList.class));
    assertTrue(ReflectionP.isSubtype(ArrayList.class, List.class));
    assertTrue(!ReflectionP.isSubtype(List.class, ArrayList.class));
  }

  /**
   * Calls the two-class overload of {@code ReflectionP.leastUpperBound}. Both formal parameters of
   * that method have type {@code Class<T>}, so it cannot be called directly on classes of two
   * different types; this method casts both arguments to {@code Class<Object>}.
   *
   * @param a a class
   * @param b a class
   * @return the least upper bound of the two classes
   */
  @SuppressWarnings("unchecked") // cast to Class<Object>
  private static @Nullable Class<Object> lub(@Nullable Class<?> a, @Nullable Class<?> b) {
    return ReflectionP.leastUpperBound((@Nullable Class<Object>) a, (@Nullable Class<Object>) b);
  }

  /**
   * Returns its arguments as an array of {@code Class<Object>}. This is needed because the formal
   * parameter of the array-of-classes overload of {@code ReflectionP.leastUpperBound} has type
   * {@code Class<T>[]}, and array types are invariant, so a {@code Class<?>[]} cannot be passed to
   * it.
   *
   * @param classes some classes
   * @return the classes, as an array of {@code Class<Object>}
   */
  @SuppressWarnings("unchecked") // cast to Class<Object>[]
  private static @Nullable Class<Object>[] classArray(@Nullable Class<?>... classes) {
    return (@Nullable Class<Object>[]) classes;
  }

  @Test
  void test_leastUpperBound() {
    // The two-classes overload.
    assertNull(ReflectionP.<Object>leastUpperBound(null, null));
    assertEquals(Integer.class, ReflectionP.leastUpperBound(Integer.class, null));
    assertEquals(Integer.class, ReflectionP.leastUpperBound(null, Integer.class));
    assertEquals(Integer.class, ReflectionP.leastUpperBound(Integer.class, Integer.class));
    // Void.TYPE is ignored.
    assertEquals(Void.TYPE, lub(Void.TYPE, Void.TYPE));
    assertEquals(Integer.class, lub(Void.TYPE, Integer.class));
    assertEquals(Integer.class, lub(Integer.class, Void.TYPE));
    // The least upper bound of a class and one of its supertypes is the supertype.
    assertEquals(Number.class, lub(Integer.class, Number.class));
    assertEquals(Number.class, lub(Number.class, Integer.class));
    // Integer and String have no unique least upper bound: each implements both Comparable and
    // Serializable, and neither is assignable from the other.
    assertThrows(Error.class, () -> lub(Integer.class, String.class));

    // The array-of-classes overload.
    assertNull(ReflectionP.leastUpperBound(classArray()));
    assertNull(ReflectionP.leastUpperBound(classArray(null, null)));
    // A null element is ignored.
    assertEquals(Integer.class, ReflectionP.leastUpperBound(classArray(null, Integer.class)));
    assertEquals(
        Number.class, ReflectionP.leastUpperBound(classArray(Integer.class, Number.class)));
    assertEquals(
        Object.class,
        ReflectionP.leastUpperBound(classArray(Integer.class, Number.class, Object.class)));

    // The array-of-objects overload.
    assertNull(ReflectionP.leastUpperBound(new Object[0]));
    assertNull(ReflectionP.leastUpperBound(new @Nullable Object[] {null, null}));
    // A null element is ignored.
    assertEquals(
        Integer.class,
        ReflectionP.leastUpperBound(new @Nullable Object[] {null, Integer.valueOf(5)}));
    assertEquals(
        Integer.class,
        ReflectionP.leastUpperBound(new Object[] {Integer.valueOf(1), Integer.valueOf(2)}));
    // The least upper bound of a class and one of its supertypes is the supertype.
    assertEquals(
        Object.class, ReflectionP.leastUpperBound(new Object[] {new Object(), Integer.valueOf(1)}));
    assertEquals(
        Object.class, ReflectionP.leastUpperBound(new Object[] {Integer.valueOf(1), new Object()}));
    assertThrows(
        Error.class,
        () -> ReflectionP.leastUpperBound(new Object[] {Integer.valueOf(1), "a string"}));

    // The list-of-objects overload.
    assertNull(ReflectionP.leastUpperBound(List.of()));
    // These use Arrays.asList rather than List.of, because List.of forbids null elements.
    assertNull(ReflectionP.leastUpperBound(Arrays.asList(null, null)));
    // A null element is ignored.
    assertEquals(
        Integer.class, ReflectionP.leastUpperBound(Arrays.asList(null, Integer.valueOf(5))));
    assertEquals(
        Integer.class,
        ReflectionP.leastUpperBound(List.of(Integer.valueOf(1), Integer.valueOf(2))));
    assertEquals(
        Object.class, ReflectionP.leastUpperBound(List.of(new Object(), Integer.valueOf(1))));
    assertThrows(
        Error.class, () -> ReflectionP.leastUpperBound(List.of(Integer.valueOf(1), "a string")));
  }

  @Test
  void test_methodForName() {
    // public static Method methodForName(String methodname) throws ClassNotFoundException
    //
    // Just test that the method is found (return value is non-null and non-erroneous).
    try {
      assertNotNull(
          ReflectionP.methodForName(
              "org.plumelib.reflection.ReflectionP.methodForName"
                  + "(java.lang.String, java.lang.String, java.lang.Class[])"));
      assertNotNull(
          ReflectionP.methodForName(
              "org.plumelib.reflection.ReflectionP.methodForName"
                  + "(java.lang.String,java.lang.String,java.lang.Class[])"));
      assertNotNull(ReflectionP.methodForName("java.lang.Math.min(int,int)"));
    } catch (Exception e) {
      e.printStackTrace();
      throw new Error(e);
    }
    try {
      ReflectionP.methodForName("org.plumelib.reflection.ReflectionP.methodForName()");
      throw new Error("Didn't throw NoSuchMethodException");
    } catch (NoSuchMethodException e) {
      // nothing to do; this is the expected case
    } catch (Exception e) {
      e.printStackTrace();
      throw new Error(e);
    }
  }
}
