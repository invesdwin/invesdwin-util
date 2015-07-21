# invesdwin-norva
norva stands for **N**aked **O**bjects **R**eflection **V**isitor **A**PI

A unified visitor pattern implementation for processing Objects, Classes and javax.model via reflection. Allowing simpler creation of code generators or UI binding frameworks following the principles of the naked objects pattern.

## Maven

Releases and snapshots are deployed to this maven repository:
```
http://invesdwin.de:8081/artifactory/invesdwin-oss
```

Dependency declaration:
```xml
<dependency>
	<groupId>de.invesdwin</groupId>
	<artifactId>invesdwin-norva</artifactId>
	<version>1.0.0-SNAPSHOT</version>
</dependency>
```


## Sample Code Generators
There are three sample annotation processors included that generate code by using this api:

### Static Facade
```java
de.invesdwin.norva.apt.staticfacade.internal.StaticFacadeDefinitionAnnotationProcessor
```
- this one can be used to extend static utility classes that are final or to combine multiple utility classes into one for simpler access by faking polymorphism
- use the `@StaticFacadeDefinition` annotation to enable this generator
- Sample:
```java
@StaticFacadeDefinition(name = "de.invesdwin.common.lang.internal.AReflectionsStaticFacade", targets = {
        org.fest.reflect.core.Reflection.class, DynamicInstrumentationReflections.class, BeanPathReflections.class,
        org.springframework.core.GenericTypeResolver.class })
public final class Reflections extends AReflectionsStaticFacade {
```

### Constants
```java
de.invesdwin.norva.apt.constants.internal.ConstantsAnnotationProcessor
```
- this one generates XyzConstants with bean path constants like "some.path.inner" for complex beans
- use the `@BeanPathRoot` to enable this generator, you can use `@NoBeanPathRoot` to exclude classes again that extend a `@BeanPathRoot` annotated base class
- Sample:
```java
@BeanPathRoot
public abstract class AValueObject implements Serializable {
```

### Build Version
```java
de.invesdwin.norva.apt.buildversion.internal.BuildVersionDefinitionAnnotationProcessor
```
- this one generates a class with a timestamp denoting the time of the build
- use the `@BuildVersionDefinition` to enable this generator
- Sample:
```java
@BuildVersionDefinition(name = "de.invesdwin.common.system.internal.ABuildVersion")
public class BuildVersion extends ABuildVersion {
```

## Bean Paths and Naked Objects

Bean Path Elements can be either properties (text fields, tables, combo boxes, etc) or actions (buttons, links, etc). 

A bean path consists of elements separated by `.`, e.g. `some.path.doSomething`.
Here `some` is the bean path root container, having a `SomeType getPath()` property method that returns a type that acts as a child container that has a `void doSomething()` action method.

Using this framework, you can easily understand bean paths and handle static and dynamic information contained in them.
They can be used to define models for generated UIs via the naked objects pattern. This framework does not do the UI generation part, instead it focuses on the reflection and basic functionality of a naked objects model and the processing of it. The actual naked objects framework can be built on top of this API, just like it is easy to create other code generators using this.

The element classes of this API provide methods for easily understanding a few annotations and utility methods and the hierarchy of when which one should override another. Also when processing objects you are able to utilize property modifiers and action invokers to ease interaction with the model.

### Bean Annotations

This framework handles the following annotations:

* `@ColumnOrder`: to define an order for properties and actions or effectively table columns, can also be used to hide columns that are not named in this annotation
* `@Disabled`: can be used to make an element disabled
* `@Hidden`: can be used to hide an element
* `@Intercept`: can be used to override bean paths of children, effectively changing the tree
* `@Tabbed`: can be used to create tabbed panels
* `@Title`: can be used to set a title text for this element
* `@Tooltip`: can be used to set a tooltip text for this element

The framework also understands `@NotNull` from the BeanValidation annotations and `@Column(nullable=false)` from the JPA annotations to determine if `null` is a valid value in choices for comboboxes.

### Utility Methods

For bean path elements you can also add utility methods for various dynamic decisions:

* `List<?> getXyzChoice()`: this can be used to define the choices a combo box has
* `List<String> columnOrder()`: this can be used to change the column order of table columns dynamically
* `String title()`: with this you can define a title text for a container
* `String disableXyz()`: with this you can dynamically disable elements, the return type can also be a boolean, when it is a string it denotes the reason why it is disabled (can be shown as a tooltip in the UI)
* `String hideXyz()`: just as the disable utility method, only that it hides elements
* `String xyzTitle()`: can be used to define a dynamic title for elements
* `String xyzTooltip()`: just like the title utility method, only for tooltips
* `boolean validateXyz(Object newValue)`: can be used to write complex validations for input, e.g. when BeanValidation annotations are not enough
* `void removeFromXyz(Object removedValue)`: can be used as a column in a table that should remove an element in the model
