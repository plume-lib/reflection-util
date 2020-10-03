# Reflection-Util change log

## 1.0.2

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

## 1.0.1

- Make ReflectionUtil.classpathToString work on Java 11

## 1.0.0

- Release 1.0.0.

## 0.2.2

- New methods binaryNameToFullyQualified and internalFormToFullyQualified

## 0.2.1

- New method ReflectionPlume.classpathToString

## 0.2.0

- Rename Signatures.classfilenameToBinaryName to classfilenameToBaseName
- New method Signatures.classfilenameToBinaryName returns qualified name

## 0.1.2

- Bug fix

## 0.1.0

- Rename ClassGetNameAndDimensions to ClassnameAndDimensions
- Add methods in class Signatures:
   - addPackage
   - classfilenameToBinaryName
   - isBinaryName
   - isDotSeparatedIdentifiers
   - isFqBinaryName

## 0.0.4

- Add methods in class Signatures:
   - getArrayElementType
   - internalFormToBinaryName
   - internalFormToClassGetName
   - isClassGetName

## 0.0.3

- Reduce dependencies on other projects

## 0.0.2

- Bug fix for `classForName`

## 0.0.1

- Initial release
