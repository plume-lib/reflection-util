# Reflection-Util change log

## 1.0.5 (2022-07-12)

- Tested under JDK 17.

## 1.0.4 (2021-07-20)

- Tested under JDK 16.

## 1.0.3 (2021-01-01)

- New method in class ReflectionPlume:
   - nameWithoutPackage

## 1.0.2 (2020-10-03)

- New methods in class Signatures:
   - isArrayWithoutPackage
   - isBinaryNameWithoutPackage
   - isBinaryNameOrPrimitiveType
   - isClassGetSimpleName
   - isDotSeparatedIdentifiersOrPrimitiveType
   - isFieldDescriptor
   - isFieldDescriptorWithoutPackage
   - isFieldDescriptorForPrimitive
   - isFullyQualifiedName
   - isIdentifier
   - isIdentifierOrPrimitiveType
   - isInternalForm
   - isPrimitiveType
- New class SignatureRegexes
- Bug fixes

## 1.0.1 (2020-09-08)

- Make ReflectionUtil.classpathToString work on Java 11

## 1.0.0 (2020-09-01)

- Release 1.0.0.

## 0.2.2 (2020-02-18)

- New methods binaryNameToFullyQualified and internalFormToFullyQualified

## 0.2.1 (2020-01-25)

- New method ReflectionPlume.classpathToString

## 0.2.0 (2019-12-16)

- Rename Signatures.classfilenameToBinaryName to classfilenameToBaseName
- New method Signatures.classfilenameToBinaryName returns qualified name

## 0.1.2 (2019-10-27)

- Bug fix

## 0.1.0 (2019-10-27)

- Rename ClassGetNameAndDimensions to ClassnameAndDimensions
- Add methods in class Signatures:
   - addPackage
   - classfilenameToBinaryName
   - isBinaryName
   - isDotSeparatedIdentifiers
   - isFqBinaryName

## 0.0.4 (2019-06-04)

- Add methods in class Signatures:
   - getArrayElementType
   - internalFormToBinaryName
   - internalFormToClassGetName
   - isClassGetName

## 0.0.3 (2019-05-30)

- Reduce dependencies on other projects

## 0.0.2 (2019-01-15)

- Bug fix for `classForName`

## 0.0.1

- Initial release
