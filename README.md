<div align="center">

<pre style="color: #87CEEB;">
██╗    ██╗██╗███╗   ██╗████████╗███████╗██████╗
██║    ██║██║████╗  ██║╚══██╔══╝██╔════╝██╔══██╗
██║ █╗ ██║██║██╔██╗ ██║   ██║   █████╗  ██████╔╝
██║███╗██║██║██║╚██╗██║   ██║   ██╔══╝  ██╔══██╗
╚███╔███╔╝██║██║ ╚████║   ██║   ███████╗██║  ██║
 ╚══╝╚══╝ ╚═╝╚═╝  ╚═══╝   ╚═╝   ╚══════╝╚═╝  ╚═╝
</pre>

</div>
**Winter Framework**

*A lightweight Java framework built from scratch to understand how frameworks work internally.*

</div>

# ❄️ Winter Framework

> A lightweight Spring-inspired Java framework built from scratch to understand how modern Java frameworks work internally.

**Winter** is a small Java framework inspired by the ideas behind Spring.

It is intentionally not designed to compete with Spring.

The goal is different:

> **Instead of only learning how to use a framework, build a small one and discover what is happening behind the scenes.**

Winter is an educational project for understanding concepts such as:

- Reflection
- Runtime Annotations
- Class Scanning
- Inversion of Control (IoC)
- Dependency Injection (DI)
- Dependency Graphs
- Singleton Objects
- Circular Dependency Detection
- Lifecycle Hooks
- Dynamic Proxies
- `InvocationHandler`
- Aspect-Oriented Programming (AOP)
- Interceptor Chains
- `@Before`
- `@After`
- `@Around`
- Framework Logging
- Custom Framework Exceptions

The most important idea in Winter is simple:

```text
You write classes.

Winter discovers them.

Winter creates them.

Winter connects them.

Winter wraps them.

Winter controls how they are used.
```

And that is the beginning of understanding what a framework actually does.

---

# Table of Contents

- [1. Why Winter?](#1-why-winter)
- [2. The Big Picture](#2-the-big-picture)
- [3. Project Architecture](#3-project-architecture)
- [4. A Mental Model Before We Start](#4-a-mental-model-before-we-start)
- [5. Reflection](#5-reflection)
- [6. Runtime Annotations](#6-runtime-annotations)
- [7. Class Scanning](#7-class-scanning)
- [8. The Snowball Concept](#8-the-snowball-concept)
- [9. The IoC Container](#9-the-ioc-container)
- [10. Dependency Injection](#10-dependency-injection)
- [11. Constructor Injection](#11-constructor-injection)
- [12. Building the Dependency Graph](#12-building-the-dependency-graph)
- [13. Interface-Based Dependency Injection](#13-interface-based-dependency-injection)
- [14. The Snowball Registry](#14-the-snowball-registry)
- [15. Singleton Behavior](#15-singleton-behavior)
- [16. Circular Dependencies](#16-circular-dependencies)
- [17. Snowball Lifecycle](#17-snowball-lifecycle)
- [18. Dynamic Proxy](#18-dynamic-proxy)
- [19. InvocationHandler](#19-invocationhandler)
- [20. How Winter Creates a Proxy](#20-how-winter-creates-a-proxy)
- [21. AOP](#21-aop)
- [22. Before Advice](#22-before-advice)
- [23. After Advice](#23-after-advice)
- [24. Around Advice](#24-around-advice)
- [25. MethodInvocation](#25-methodinvocation)
- [26. InterceptorChain](#26-interceptorchain)
- [27. The AOP Call Stack](#27-the-aop-call-stack)
- [28. Logging](#28-logging)
- [29. Custom Exceptions](#29-custom-exceptions)
- [30. Complete Startup Flow](#30-complete-startup-flow)
- [31. Complete Invocation Flow](#31-complete-invocation-flow)
- [32. Example Application](#32-example-application)
- [33. What Happens When `cat.meow()` Is Called?](#33-what-happens-when-catmeow-is-called)
- [34. Project Structure](#34-project-structure)
- [35. Mapping Winter to Spring Concepts](#35-mapping-winter-to-spring-concepts)
- [36. Intentional Limitations](#36-intentional-limitations)
- [37. Design Decisions](#37-design-decisions)
- [38. Setup](#38-setup)
- [39. Running the Example](#39-running-the-example)
- [40. Example Output](#40-example-output)
- [41. Troubleshooting](#41-troubleshooting)
- [42. Experiments to Try](#42-experiments-to-try)
- [43. What to Build Next](#43-what-to-build-next)
- [44. Final Mental Model](#44-final-mental-model)
- [45. Final Cheat Sheet](#45-final-cheat-sheet)

---

# 1. Why Winter?

When learning a framework, it is very easy to write:

```java
@Autowired
private UserService userService;
```

or:

```java
ApplicationContext context = ...;
UserService service = context.getBean(UserService.class);
```

and simply accept that the framework somehow makes everything work.

But what actually happens?

How does a framework know which classes exist?

How does it find annotations?

How does it instantiate an object whose constructor requires another object?

How does it know which implementation to inject when you ask for an interface?

How can it execute code before and after a method without modifying the method itself?

How does:

```java
service.doSomething();
```

turn into:

```text
proxy
  ↓
interceptor
  ↓
target method
```

?

These are the questions Winter tries to answer.

The project deliberately starts with basic Java mechanisms and gradually builds framework-like behavior on top of them.

The result is much more useful than memorizing framework annotations:

```text
Reflection
    ↓
Annotations
    ↓
Scanning
    ↓
Object creation
    ↓
Dependency resolution
    ↓
IoC
    ↓
Proxy
    ↓
AOP
```

Once these pieces click together, a framework stops looking like magic.

---

# 2. The Big Picture

At a very high level, Winter performs this process:

```text
                    YOUR APPLICATION
                           |
                           v
                  ┌─────────────────┐
                  │ WinterContext   │
                  └────────┬────────┘
                           |
                           v
                    Class Scanning
                           |
                           v
                 Find @Snowball classes
                           |
                           v
                 Register implementations
                           |
                           v
                Create Snowball objects
                           |
                           v
                Resolve dependencies
                           |
                           v
                  Constructor Injection
                           |
                           v
                    Lifecycle hooks
                           |
                           v
                    Dynamic Proxy
                           |
                           v
                         AOP
                           |
                           v
                   Snowball Registry
                           |
                           v
                   Application uses it
```

The important thing is that **the application does not manually build the entire object graph**.

Instead of:

```java
FoodService foodService = new FoodService();

CatService catService =
        new CatService(foodService);

ZooService zooService =
        new ZooService(catService);
```

Winter allows the application to describe relationships:

```java
@Snowball
class CatService {

    private final FoodService foodService;

    public CatService(FoodService foodService) {
        this.foodService = foodService;
    }
}
```

Winter figures out the rest.

---

# 3. Project Architecture

Winter currently consists of several small parts.

```text
com.winter
│
├── context
│   └── WinterContext
│
├── annotation
│   ├── Snowball
│   ├── PostSnowball
│   ├── Before
│   ├── After
│   └── Around
│
├── scanner
│   └── ClassScanner
│
├── registry
│   └── SnowballRegistry
│
├── proxy
│   ├── ProxyFactory
│   └── WinterInvocationHandler
│
├── aop
│   ├── MethodInterceptor
│   ├── MethodInvocation
│   ├── InterceptorChain
│   ├── BeforeInterceptor
│   ├── AfterInterceptor
│   └── AroundInterceptor
│
├── logging
│   └── WinterLogger
│
├── exception
│   ├── WinterException
│   ├── SnowballCreationException
│   ├── DependencyException
│   └── CircularDependencyException
│
└── example
    ├── CatService
    ├── CatOperations
    ├── ZooService
    └── services
        ├── FoodService
        └── DogService
```

Each component has a focused responsibility.

---

# 4. A Mental Model Before We Start

Before diving into code, imagine Winter as a small factory.

You give it classes:

```text
CatService
FoodService
ZooService
DogService
```

Winter asks:

```text
Which classes are Snowballs?
        ↓
Which interfaces do they implement?
        ↓
What does each constructor require?
        ↓
Where can those dependencies be found?
        ↓
Do those dependencies need to be created?
        ↓
Should the object have a lifecycle callback?
        ↓
Should it be proxied?
        ↓
Does the proxy have AOP behavior?
        ↓
Store the resulting object.
```

This is essentially a runtime object graph builder.

That sentence is one of the most important ideas in the entire project:

> **An IoC container is fundamentally responsible for constructing and managing an object graph.**

---

# 5. Reflection

Reflection is one of the foundations of Winter.

Normally Java code is strongly tied to classes at compile time.

For example:

```java
CatService cat = new CatService(foodService);
```

The programmer explicitly knows:

- the class
- the constructor
- the dependency
- when the object is created

Reflection allows us to inspect classes **at runtime**.

For example:

```java
Class<?> clazz = CatService.class;
```

Now we have a runtime representation of the class.

We can ask it questions:

```java
clazz.getName();
clazz.getInterfaces();
clazz.getDeclaredConstructors();
clazz.getDeclaredMethods();
clazz.isAnnotationPresent(Snowball.class);
```

This changes the programming model.

Instead of:

```text
Programmer
   ↓
"I know CatService."
   ↓
new CatService(...)
```

we can have:

```text
Framework
   ↓
"I discovered this Class object."
   ↓
"What is this class?"
   ↓
"What annotations does it have?"
   ↓
"What constructor does it have?"
   ↓
"What dependencies does it require?"
```

That is a huge part of what frameworks do.

---

## Creating an object using Reflection

Winter can discover a constructor:

```java
Constructor<?> constructor =
        clazz.getDeclaredConstructors()[0];
```

Then instantiate it:

```java
Object object =
        constructor.newInstance(dependencies);
```

Conceptually:

```text
Class
 │
 ├── Constructor
 │      │
 │      └── Parameters
 │
 └── Methods
```

Reflection turns these pieces into runtime objects that the framework can inspect.

---

## Why is Reflection important for a framework?

Without reflection, Winter would need hard-coded knowledge:

```java
new CatService(...);
new FoodService();
new DogService();
new ZooService(...);
```

That is not a framework.

It is just application code.

Reflection allows Winter to operate on classes it did not explicitly hard-code.

---

# 6. Runtime Annotations

Winter uses annotations as metadata.

The most important one is:

```java
@Snowball
public class FoodService {
}
```

The annotation itself is:

```java
@Retention(RUNTIME)
@Target(TYPE)
public @interface Snowball {
}
```

The most important part is:

```java
@Retention(RUNTIME)
```

Why?

Because Winter needs to inspect the annotation while the application is running:

```java
clazz.isAnnotationPresent(Snowball.class);
```

If the annotation disappeared before runtime, Winter could not discover it.

---

## Annotation lifecycle

Think about it like this:

```text
Source Code
    ↓
Compile
    ↓
.class file
    ↓
Runtime
    ↓
Reflection
    ↓
"Does this class have @Snowball?"
```

Runtime retention keeps the metadata available during the final step.

---

# 7. Class Scanning

Reflection can inspect a class **if we already have the class**.

But Winter has a bigger problem:

> How do we discover the classes in the first place?

That is the job of `ClassScanner`.

The application gives Winter a base package:

```text
com.winter.example
```

Winter converts the package into a path:

```text
com.winter.example
        ↓
com/winter/example
```

Then it searches that directory recursively.

Conceptually:

```text
com/winter/example
│
├── CatService.class
├── ZooService.class
│
└── services
    ├── FoodService.class
    └── DogService.class
```

The scanner discovers class files and converts their names back into Java class names:

```text
com.winter.example.CatService
com.winter.example.ZooService
com.winter.example.services.FoodService
com.winter.example.services.DogService
```

Then it loads them:

```java
Class.forName(className);
```

Now Winter has:

```java
List<Class<?>> classes;
```

and can inspect them.

---

## Scanning flow

```text
basePackage
    │
    v
ClassScanner
    │
    v
Find .class files
    │
    v
Build class names
    │
    v
Class.forName(...)
    │
    v
List<Class<?>>
```

Then:

```text
Every discovered class
        │
        ├── @Snowball ?
        │       │
        │       ├── YES → Winter manages it
        │       │
        │       └── NO  → ignore
        │
        └── continue
```

---

# 8. The Snowball Concept

Spring has a concept called a **Bean**.

Winter uses the name **Snowball**.

A Snowball is simply an object that Winter manages.

For example:

```java
@Snowball
public class FoodService {

    public void feed() {
        System.out.println("Food served!");
    }
}
```

The annotation tells Winter:

> "This class belongs to the framework's object management system."

Without `@Snowball`, Winter ignores the class during startup.

---

# 9. The IoC Container

IoC means:

> **Inversion of Control**

Normally, your code controls object creation.

```java
FoodService food = new FoodService();
CatService cat = new CatService(food);
```

The application says:

```text
Create this.
Then create that.
Connect them together.
```

With IoC, the framework takes control.

```text
Application
     │
     │ declares classes + dependencies
     ▼
   Winter
     │
     ├── creates objects
     ├── resolves dependencies
     ├── manages lifecycle
     └── stores objects
```

The control of object creation has moved from application code to the framework.

That is the "Inversion" in Inversion of Control.

---

# 10. Dependency Injection

DI is one of the mechanisms used to achieve IoC.

Suppose:

```java
@Snowball
public class CatService {

    private final FoodService foodService;

    public CatService(FoodService foodService) {
        this.foodService = foodService;
    }
}
```

`CatService` depends on `FoodService`.

Without DI:

```java
FoodService food =
        new FoodService();

CatService cat =
        new CatService(food);
```

With Winter:

```text
Winter
  │
  ├── sees CatService
  │
  ├── sees constructor
  │
  ├── finds FoodService parameter
  │
  ├── resolves FoodService
  │
  ├── creates FoodService
  │
  └── calls:
          new CatService(foodService)
```

The dependency is **injected** into the object.

---

# 11. Constructor Injection

Winter's v1 design intentionally uses constructor injection.

For example:

```java
public CatService(FoodService foodService) {
    this.foodService = foodService;
}
```

Winter inspects:

```java
Constructor<?> constructor =
        findConstructor(clazz);
```

The current rule is simple:

> A Snowball must have exactly one constructor.

The framework checks:

```java
Constructor<?>[] constructors =
        clazz.getDeclaredConstructors();

if (constructors.length != 1) {
    throw new SnowballCreationException(...);
}
```

Why impose this restriction?

Because it makes dependency resolution deterministic.

Winter does not need to guess:

```text
Which constructor should I use?

Constructor A?
Constructor B?
Constructor C?
```

Instead:

```text
Snowball
   ↓
exactly one constructor
   ↓
constructor parameters
   ↓
dependencies
```

This is intentionally simpler than a production framework.

---

# 12. Building the Dependency Graph

This is one of the most important concepts in Winter.

Consider:

```java
@Snowball
class CatService {

    public CatService(FoodService foodService) {
    }
}
```

The dependency graph is:

```text
CatService
    │
    └── depends on
            ↓
       FoodService
```

Now add:

```java
@Snowball
class ZooService {

    public ZooService(CatOperations cat) {
    }
}
```

The graph becomes:

```text
ZooService
    │
    └── CatOperations
            │
            └── implemented by
                    ↓
                CatService
                    │
                    └── FoodService
```

So Winter is effectively working with:

```text
ZooService
    ↓
CatOperations
    ↓
CatService
    ↓
FoodService
```

This is a directed dependency graph.

---

## How Winter traverses the graph

Suppose Winter starts with:

```text
ZooService
```

It sees:

```java
ZooService(CatOperations cat)
```

So it asks:

```text
"I need CatOperations."
```

Then it finds:

```text
CatOperations → CatService
```

Now Winter needs:

```text
CatService
```

It inspects:

```java
CatService(FoodService foodService)
```

So it needs:

```text
FoodService
```

`FoodService` has no dependencies.

Therefore:

```text
FoodService
    ↓
created first

CatService
    ↓
created using FoodService

ZooService
    ↓
created using CatService
```

This is essentially a recursive graph traversal.

---

## Visualizing the creation order

```text
                 ZooService
                     │
                     │ needs
                     ▼
               CatOperations
                     │
                     │ resolves to
                     ▼
                 CatService
                     │
                     │ needs
                     ▼
                FoodService
                     │
                     │ no dependencies
                     ▼
                   CREATE
                     │
                     ▼
              CatService CREATE
                     │
                     ▼
              ZooService CREATE
```

This is one of the main "aha" moments of the project.

Dependency Injection is not magic.

At a basic level:

> **The container recursively resolves a dependency graph and constructs objects from the bottom of that graph upward.**

---

# 13. Interface-Based Dependency Injection

Winter also supports injecting interfaces.

Consider:

```java
public interface CatOperations {

    void meow();
}
```

and:

```java
@Snowball
public class CatService
        implements CatOperations {
}
```

Now another Snowball can depend on:

```java
public ZooService(CatOperations cat) {
    this.cat = cat;
}
```

The problem is obvious:

```text
CatOperations
```

is an interface.

You cannot do:

```java
new CatOperations();
```

So Winter maintains an implementation map:

```text
Interface
    ↓
Implementation

CatOperations
    ↓
CatService
```

During startup, Winter registers the relationship:

```java
implementations.put(
        interfaceType,
        clazz
);
```

Then dependency resolution becomes:

```text
Need CatOperations
       ↓
Registry?
       ↓
No
       ↓
Is CatOperations itself a Snowball?
       ↓
No
       ↓
Look inside implementations
       ↓
CatOperations → CatService
       ↓
Create CatService
```

This is another important framework idea:

> **An abstraction can be used as the dependency contract while the container decides which concrete implementation satisfies it.**

---

# 14. The Snowball Registry

After Winter creates an object, it needs somewhere to keep it.

That is the purpose of:

```text
SnowballRegistry
```

Internally it maintains a map:

```text
Class<?> → Object
```

Conceptually:

```text
SnowballRegistry
│
├── FoodService.class → FoodService object
├── CatService.class  → CatService object
├── CatOperations.class → Cat proxy
└── ZooService.class  → ZooService object
```

The registry allows Winter to answer:

```java
registry.get(FoodService.class);
```

and return the already-created object.

---

## Why store interfaces too?

Suppose:

```text
CatService implements CatOperations
```

and Winter creates a proxy.

The application may ask for:

```java
CatOperations.class
```

Therefore the registry also maps interfaces to the exposed object.

This allows:

```text
CatOperations
      ↓
    proxy
```

instead of requiring application code to know about the implementation.

---

# 15. Singleton Behavior

Winter's Snowballs are singleton-like **within a `WinterContext`**.

Suppose:

```java
FoodService a =
        context.getSnowball(FoodService.class);

FoodService b =
        context.getSnowball(FoodService.class);
```

The intention is that:

```text
a == b
```

because Winter does not repeatedly create the same Snowball.

The creation process first checks the registry:

```java
Object existing =
        registry.get(clazz);

if (existing != null) {
    return existing;
}
```

So:

```text
Request Snowball
      │
      ▼
Registry
      │
      ├── exists → return existing object
      │
      └── missing → create object
```

This is one of the core responsibilities of a container.

---

# 16. Circular Dependencies

Consider:

```java
class A {

    public A(B b) {
    }
}
```

and:

```java
class B {

    public B(A a) {
    }
}
```

The dependency graph becomes:

```text
A
↓
B
↓
A
↓
B
↓
A
...
```

A naive recursive algorithm would never stop.

Winter therefore keeps track of classes currently being created:

```java
Set<Class<?>> creatingSnowballs;
```

Before creating a Snowball:

```text
Is this class already being created?
```

If yes:

```text
Circular dependency detected
```

The logic is essentially:

```text
create A
  │
  └── create B
         │
         └── create A
                │
                └── A is already being created
                       ↓
                 ERROR
```

Winter reports this using:

```java
CircularDependencyException
```

---

# 17. Snowball Lifecycle

Creating an object is not always the end of its lifecycle.

Winter supports:

```java
@PostSnowball
public void init() {
    ...
}
```

Example:

```java
@Snowball
public class FoodService {

    @PostSnowball
    public void init() {
        System.out.println(
                "FoodService initialized!"
        );
    }
}
```

After creating the object, Winter scans its methods:

```java
for (Method method :
        clazz.getDeclaredMethods()) {
```

Then checks:

```java
method.isAnnotationPresent(
        PostSnowball.class
)
```

If found:

```java
method.invoke(snowball);
```

So:

```text
Create object
     ↓
Initialize object
     ↓
Execute @PostSnowball
     ↓
Expose object
```

---

## Lifecycle graph

```text
Constructor
    ↓
Object created
    ↓
@PostSnowball
    ↓
Object ready
```

This is conceptually similar to lifecycle callbacks found in larger dependency-injection frameworks.

---

# 18. Dynamic Proxy

Now we reach one of the most interesting parts.

Java allows us to create objects that look like implementations of interfaces but actually intercept method calls.

Suppose:

```java
public interface CatOperations {

    void meow();
}
```

and:

```java
public class CatService
        implements CatOperations {

    @Override
    public void meow() {
        System.out.println("Meow!");
    }
}
```

Normally:

```java
CatOperations cat =
        new CatService();

cat.meow();
```

The call goes directly to `CatService`.

With a dynamic proxy:

```text
Application
    │
    │ cat.meow()
    ▼
  Proxy
    │
    ▼
InvocationHandler
    │
    ▼
 CatService
    │
    ▼
 meow()
```

The proxy stands between the caller and the real object.

This is the foundation that Winter uses for AOP.

---

# 19. InvocationHandler

Java's JDK dynamic proxy mechanism uses:

```java
InvocationHandler
```

Winter implements:

```text
WinterInvocationHandler
```

Its most important method is:

```java
public Object invoke(
        Object proxy,
        Method method,
        Object[] args
) throws Throwable
```

This method is called when the proxy receives a method invocation.

So when the application does:

```java
cat.meow();
```

the proxy can redirect the call to:

```java
invoke(
    proxy,
    meowMethod,
    args
);
```

The handler can then decide what happens next.

---

## The key idea

Without proxy:

```text
caller
  ↓
real object
```

With proxy:

```text
caller
  ↓
proxy
  ↓
handler
  ↓
real object
```

That middle layer is extremely powerful.

It allows us to execute framework behavior without modifying the business method itself.

---

# 20. How Winter Creates a Proxy

Winter uses:

```java
Proxy.newProxyInstance(...)
```

inside:

```text
ProxyFactory
```

The factory receives the real object:

```java
ProxyFactory.createProxy(snowball);
```

It gets the interfaces:

```java
Class<?>[] interfaces =
        target.getClass().getInterfaces();
```

Then creates:

```java
Proxy.newProxyInstance(
        target.getClass().getClassLoader(),
        interfaces,
        new WinterInvocationHandler(target)
);
```

Conceptually:

```text
CatService object
      │
      │ implements
      ▼
CatOperations
      │
      ▼
Proxy.newProxyInstance(...)
      │
      ▼
$Proxy0
```

`$Proxy0` is an automatically generated runtime proxy class.

You do not write `$Proxy0`.

The JVM creates it.

---

# 21. AOP

AOP means:

> **Aspect-Oriented Programming**

The basic idea is to separate cross-cutting behavior from business logic.

Imagine:

```java
public void meow() {
    System.out.println("Meow!");
}
```

Now you want:

```text
log before method
log after method
measure execution time
check permissions
open transaction
close transaction
```

You could put everything inside `meow()`.

But that would mix business logic with framework concerns.

AOP gives us another approach:

```text
Business method
      +
Cross-cutting behavior
```

Winter implements a very small version of this idea.

---

# 22. Before Advice

Winter supports:

```java
@Before("meow")
public void beforeMeow() {
    System.out.println("Before Meow!");
}
```

The intention is:

```text
Before advice
      ↓
Real method
```

So:

```text
@Before
   ↓
meow()
```

The interceptor:

1. Finds methods annotated with `@Before`
2. Checks whether the annotation targets the current method
3. Invokes the advice
4. Calls `invocation.proceed()`

Conceptually:

```text
BeforeInterceptor
       │
       ├── execute advice
       │
       └── proceed()
              ↓
          next interceptor
```

---

# 23. After Advice

Winter also supports:

```java
@After("meow")
public void afterMeow() {
    System.out.println("After Meow!");
}
```

The important difference is where `proceed()` happens.

The interceptor first calls:

```java
Object result =
        invocation.proceed();
```

Only after that returns does it execute the advice.

So:

```text
proceed()
   ↓
real method
   ↓
return
   ↓
@After
```

Therefore the current Winter implementation is conceptually closer to:

> **after returning**

rather than a `finally`-style "always after".

If the method throws an exception before returning, the current `@After` advice does not execute.

That is an intentional v1 simplification.

---

# 24. Around Advice

`@Around` is the most powerful advice type in Winter.

Example:

```java
@Around("meow")
public Object aroundMeow(
        MethodInvocation invocation
) throws Throwable {

    System.out.println("Around Before");

    Object result =
            invocation.proceed();

    System.out.println("Around After");

    return result;
}
```

This gives the advice control over the invocation itself.

Think of:

```java
invocation.proceed();
```

as:

> "Continue to whatever comes next."

That "whatever comes next" might be:

```text
another interceptor
```

or eventually:

```text
the real method
```

---

## Around can modify return values

Suppose:

```java
String name() {
    return "Milo";
}
```

An around advice can do:

```java
Object result =
        invocation.proceed();

return "Snow-" + result;
```

Now:

```text
Real method
    ↓
"Milo"
    ↓
Around advice
    ↓
"Snow-Milo"
```

The caller sees:

```text
Snow-Milo
```

---

## Around can block execution

This is even more important.

Consider:

```java
@Around("name")
public Object blockName(
        MethodInvocation invocation
) {
    return "Unknown";
}
```

Notice:

```java
invocation.proceed();
```

is missing.

Therefore:

```text
Caller
  ↓
Around
  ↓
return "Unknown"
  X
Real method never runs
```

This is why `Around` is essentially a wrapper around the invocation.

---

# 25. MethodInvocation

`MethodInvocation` represents the current method call.

It contains:

```text
target
method
arguments
chain
```

Conceptually:

```text
MethodInvocation
│
├── target
│     └── real CatService object
│
├── method
│     └── meow()
│
├── args
│     └── method arguments
│
└── chain
      └── interceptor chain
```

It exposes:

```java
getTarget()
getMethod()
getArgs()
proceed()
```

The most important method is:

```java
proceed()
```

because it allows an interceptor to continue execution.

---

# 26. InterceptorChain

Winter uses an interceptor chain:

```java
List<MethodInterceptor> interceptors;
```

The current chain contains:

```text
AroundInterceptor
BeforeInterceptor
AfterInterceptor
```

The chain maintains an index:

```java
private int index = 0;
```

When:

```java
proceed()
```

is called, the chain checks:

```text
Are there more interceptors?
```

If yes:

```text
Take next interceptor
      ↓
invoke()
```

If no:

```text
invoke real target method
```

This is the heart of Winter's AOP implementation.

---

# 27. The AOP Call Stack

Suppose:

```java
cat.meow();
```

The complete flow is approximately:

```text
Application
    │
    ▼
JDK Proxy
    │
    ▼
WinterInvocationHandler
    │
    ▼
MethodInvocation
    │
    ▼
InterceptorChain
    │
    ▼
AroundInterceptor
    │
    ├── Around Before
    │
    ▼
BeforeInterceptor
    │
    ├── Before Meow
    │
    ▼
AfterInterceptor
    │
    ▼
Real CatService.meow()
    │
    ├── Meow!
    │
    └── FoodService.feed()
    │
    ▼
After advice
    │
    ▼
Around After
    │
    ▼
Application
```

This is the key AOP "aha moment":

> **AOP is not magic. At a basic level, it is a controlled chain of wrappers around a method invocation.**

---

# 28. Logging

Winter includes its own simple framework logger:

```text
WinterLogger
```

The framework uses:

```java
logger.log("...");
```

and outputs:

```text
[log] Starting Winter...
[log] Scanning package: com.winter.example
[log] Found Snowball: ...
[log] Resolving dependency: ...
```

The logger uses a boolean to determine whether logging is enabled.

Conceptually:

```text
WinterLogger(true)
      ↓
print logs

WinterLogger(false)
      ↓
ignore logs
```

The `[log]` prefix is displayed in an ice-blue terminal color.

Winter also has a startup banner.

The purpose is partly functional and partly educational:

> The framework should make its internal behavior visible while you are learning it.

---

# 29. Custom Exceptions

A framework should communicate framework errors clearly.

Instead of throwing only:

```java
RuntimeException
```

Winter defines its own exception hierarchy:

```text
WinterException
│
├── SnowballCreationException
│
└── DependencyException
      │
      └── CircularDependencyException
```

---

## `WinterException`

Base exception:

```java
public class WinterException
        extends RuntimeException {
}
```

This allows framework-specific failures to be identified as Winter errors.

---

## `SnowballCreationException`

Used when Winter cannot create a Snowball.

Examples:

```text
Snowball must have exactly one constructor
```

or:

```text
Could not create Snowball
```

---

## `DependencyException`

Used when Winter cannot resolve a dependency.

For example:

```text
No Snowball implementation found for:
CatOperations
```

---

## `CircularDependencyException`

A more specific dependency problem:

```text
A → B → A
```

So:

```text
CircularDependencyException
        ↓
DependencyException
        ↓
WinterException
```

The hierarchy itself communicates meaning.

---

# 30. Complete Startup Flow

Now let's combine everything.

When the application executes:

```java
context.start();
```

Winter approximately performs this:

```text
                    context.start()
                          │
                          ▼
                   WinterLogger
                     startup()
                          │
                          ▼
                    ClassScanner
                          │
                          ▼
                Scan base package
                          │
                          ▼
                 Find .class files
                          │
                          ▼
                   Load classes
                          │
                          ▼
                 Find @Snowball
                          │
                          ▼
             Register interfaces
                          │
                          ▼
                createSnowball()
                          │
                          ▼
              Check Registry
                          │
             ┌────────────┴────────────┐
             │                         │
           found                    missing
             │                         │
             ▼                         ▼
         return it             inspect constructor
                                       │
                                       ▼
                              resolve dependencies
                                       │
                                       ▼
                              recursively create
                                       │
                                       ▼
                              constructor.newInstance
                                       │
                                       ▼
                               @PostSnowball
                                       │
                                       ▼
                                  Proxy?
                                       │
                                       ▼
                               register object
                                       │
                                       ▼
                                   ready
```

This is essentially the entire container.

---

# 31. Complete Invocation Flow

After startup, the application gets a Snowball:

```java
CatOperations cat =
        context.getSnowball(
                CatOperations.class
        );
```

Then:

```java
cat.meow();
```

The flow becomes:

```text
Application
     │
     │ cat.meow()
     ▼
JDK Proxy
     │
     ▼
WinterInvocationHandler
     │
     ▼
InterceptorChain
     │
     ▼
AroundInterceptor
     │
     ▼
BeforeInterceptor
     │
     ▼
AfterInterceptor
     │
     ▼
Real CatService
     │
     ▼
meow()
```

And the result travels back upward:

```text
meow()
  ↑
AfterInterceptor
  ↑
BeforeInterceptor
  ↑
AroundInterceptor
  ↑
Proxy
  ↑
Application
```

This "down the chain / back up the chain" behavior is fundamental to understanding interceptors.

---

# 32. Example Application

Winter includes a small example application.

## FoodService

```java
@Snowball
public class FoodService {

    @PostSnowball
    public void init() {
        System.out.println(
                "FoodService initialized!"
        );
    }

    public void feed() {
        System.out.println(
                "Food served!"
        );
    }
}
```

This demonstrates:

- `@Snowball`
- lifecycle
- dependency target

---

## CatOperations

```java
public interface CatOperations {

    void meow();
}
```

This demonstrates the abstraction used for interface-based DI and proxying.

---

## CatService

```java
@Snowball
public class CatService
        implements CatOperations {

    private final FoodService foodService;

    public CatService(
            FoodService foodService
    ) {
        this.foodService = foodService;
    }

    @Before("meow")
    public void beforeMeow() {
        System.out.println(
                "Before Meow!"
        );
    }

    @Override
    public void meow() {

        System.out.println("Meow!");

        foodService.feed();
    }

    @After("meow")
    public void afterMeow() {
        System.out.println(
                "After Meow!"
        );
    }
}
```

This single class demonstrates several concepts:

```text
@Snowball
    ↓
IoC management

FoodService constructor parameter
    ↓
Dependency Injection

implements CatOperations
    ↓
Interface-based resolution

@Before
    ↓
AOP

@After
    ↓
AOP
```

---

## ZooService

```java
@Snowball
public class ZooService {

    private final CatOperations cat;

    public ZooService(
            CatOperations cat
    ) {
        this.cat = cat;
    }

    public void testCat() {
        cat.meow();
    }
}
```

This is particularly important.

`ZooService` does **not** depend directly on:

```java
CatService
```

It depends on:

```java
CatOperations
```

Winter resolves:

```text
CatOperations
      ↓
CatService
      ↓
Proxy
```

Therefore ZooService receives the proxied abstraction.

This allows the AOP system to participate in the call.

---

# 33. What Happens When `cat.meow()` Is Called?

This is the final integration example.

Suppose the application has:

```java
CatOperations cat =
        context.getSnowball(
                CatOperations.class
        );

cat.meow();
```

### Step 1 — Registry

Winter returns the object stored under:

```text
CatOperations.class
```

That object is the proxy.

```text
Registry
   │
   └── CatOperations → Proxy
```

### Step 2 — Proxy

The proxy receives:

```text
meow()
```

and forwards it to:

```text
WinterInvocationHandler
```

### Step 3 — Handler

The handler creates:

```text
AroundInterceptor
BeforeInterceptor
AfterInterceptor
```

and creates a:

```text
MethodInvocation
```

### Step 4 — Around

If `@Around` exists:

```text
Around Before
```

then:

```java
invocation.proceed();
```

### Step 5 — Before

Winter finds:

```java
@Before("meow")
```

and executes:

```text
Before Meow!
```

### Step 6 — Real method

The actual method executes:

```text
Meow!
```

Then:

```java
foodService.feed();
```

produces:

```text
Food served!
```

### Step 7 — After

After the real method returns:

```text
After Meow!
```

### Step 8 — Around returns

Finally:

```text
Around After
```

So the visible sequence can look like:

```text
Around Before
Before Meow!
Meow!
Food served!
After Meow!
Around After
```

And behind those five lines, a substantial framework pipeline has executed.

---

# 34. Project Structure

The current project is organized around framework responsibilities:

```text
src/
└── main/
    └── java/
        └── com/
            └── winter/
                │
                ├── annotation/
                │   ├── Snowball.java
                │   ├── PostSnowball.java
                │   ├── Before.java
                │   ├── After.java
                │   └── Around.java
                │
                ├── context/
                │   └── WinterContext.java
                │
                ├── scanner/
                │   └── ClassScanner.java
                │
                ├── registry/
                │   └── SnowballRegistry.java
                │
                ├── proxy/
                │   ├── ProxyFactory.java
                │   └── WinterInvocationHandler.java
                │
                ├── aop/
                │   ├── MethodInterceptor.java
                │   ├── MethodInvocation.java
                │   ├── InterceptorChain.java
                │   ├── BeforeInterceptor.java
                │   ├── AfterInterceptor.java
                │   └── AroundInterceptor.java
                │
                ├── logging/
                │   └── WinterLogger.java
                │
                ├── exception/
                │   ├── WinterException.java
                │   ├── SnowballCreationException.java
                │   ├── DependencyException.java
                │   └── CircularDependencyException.java
                │
                └── example/
                    ├── CatOperations.java
                    ├── CatService.java
                    ├── ZooService.java
                    │
                    └── services/
                        ├── FoodService.java
                        └── DogService.java
```

---

# 35. Mapping Winter to Spring Concepts

Winter is inspired by Spring, but it is **not Spring**.

The following table shows conceptual relationships:

| Winter | Conceptual Spring counterpart |
|---|---|
| `@Snowball` | `@Component` / managed Bean |
| Snowball | Bean |
| `WinterContext` | `ApplicationContext` |
| `SnowballRegistry` | Bean registry / container internals |
| `ClassScanner` | Component scanning |
| Constructor DI | Constructor-based dependency injection |
| `@PostSnowball` | Lifecycle callback such as `@PostConstruct` |
| `ProxyFactory` | Proxy infrastructure |
| `WinterInvocationHandler` | Invocation/proxy infrastructure |
| `@Before` | Before advice |
| `@After` | After advice |
| `@Around` | Around advice |
| `InterceptorChain` | Interceptor/advice chain |
| `MethodInvocation` | Invocation context |
| `WinterLogger` | Framework logging |
| `WinterException` | Framework-specific exception hierarchy |

These are **conceptual mappings**, not claims that Winter implements Spring's actual internals or API.

---

# 36. Intentional Limitations

Winter is an educational framework.

Its limitations are intentional.

## 36.1 Directory-based class scanning

The current scanner primarily works with classes available through directory-based classpath resources.

It does not attempt to implement a full production-grade JAR/classpath scanning engine.

---

## 36.2 One constructor

A Snowball must currently have exactly one constructor.

There is no:

```java
@Inject
```

constructor selection system.

---

## 36.3 JDK Dynamic Proxy

Winter currently relies on Java's interface-based dynamic proxy mechanism.

Therefore proxying is centered around interfaces.

A class without an interface is not handled by the same JDK proxy mechanism.

---

## 36.4 Simple method matching

AOP pointcuts currently use method names:

```java
@Before("meow")
```

This is intentionally simple.

A production framework can have much more expressive pointcut systems.

---

## 36.5 Simple lifecycle

Winter currently provides:

```java
@PostSnowball
```

as a basic initialization hook.

It does not attempt to implement a complete production lifecycle system.

---

## 36.6 `@After` semantics

The current `@After` interceptor executes after:

```java
invocation.proceed()
```

successfully returns.

Therefore it is closer to:

```text
after returning
```

than:

```text
finally
```

---

## 36.7 Educational architecture

`WinterContext` currently owns more responsibilities than an ideal production architecture would.

A future version can extract responsibilities such as:

```text
SnowballFactory
DependencyResolver
AopManager
```

This is intentionally left as future architectural work.

---

# 37. Design Decisions

Winter favors **clarity over production complexity**.

For example:

### One constructor

Instead of implementing constructor selection rules:

```text
@Autowired?
@Inject?
Primary?
Qualifier?
Multiple constructors?
```

Winter simply says:

```text
One Snowball → One constructor
```

This makes the DI algorithm easy to understand.

---

### Simple interfaces

Instead of a sophisticated proxy framework, Winter uses:

```text
java.lang.reflect.Proxy
```

This lets the developer learn how Java's built-in proxy mechanism works.

---

### Simple AOP

Instead of a complex pointcut language:

```java
@Before("meow")
```

the method name is enough to demonstrate the idea.

---

### Small components

The project intentionally avoids introducing large dependencies.

The point is to understand what can be built from:

```text
Java
+
Reflection
+
Annotations
+
Collections
+
JDK Proxy
```

---

# 38. Setup

Requirements:

- Java 17+
- Maven
- IntelliJ IDEA or another Java IDE

Clone the repository:

```bash
git clone <repository-url>
```

Enter the project:

```bash
cd winter-framework
```

Build:

```bash
mvn clean package
```

The project is designed around standard Java functionality and does not require the Spring Framework.

---

# 39. Running the Example

Create a context using the application's base package:

```java
WinterContext context =
        new WinterContext(
                "com.winter.example"
        );

context.start();
```

After startup, Snowballs can be retrieved from the context:

```java
CatOperations cat =
        context.getSnowball(
                CatOperations.class
        );

cat.meow();
```

Or:

```java
ZooService zoo =
        context.getSnowball(
                ZooService.class
        );

zoo.testCat();
```

The important point is that the application does not manually construct:

```java
FoodService
CatService
ZooService
```

Winter constructs and connects them.

---

# 40. Example Output

A startup may produce framework logs such as:

```text
[log] Starting Winter...
[log] Scanning package: com.winter.example
[log] Found classes
[log] Found Snowball: com.winter.example.CatService
[log] Resolving dependency: com.winter.example.services.FoodService
[log] Creating Snowball: com.winter.example.services.FoodService
[log] Initializing Snowball: com.winter.example.services.FoodService
[log] Executing @PostSnowball: init
FoodService initialized!
[log] Creating proxy for: com.winter.example.CatService
```

Then a method invocation can produce:

```text
[log] Intercepting method: meow
Around Before
Before Meow!
Meow!
Food served!
After Meow!
Around After
```

Notice the difference:

```text
[log] ...
```

is framework behavior.

Whereas:

```text
Meow!
Food served!
```

is application/business behavior.

That distinction is important.

---

# 41. Troubleshooting

## `No Snowball implementation found`

Example:

```text
No Snowball implementation found for:
CatOperations
```

This usually means Winter cannot find a registered implementation.

Check that the implementation is annotated:

```java
@Snowball
public class CatService
        implements CatOperations {
}
```

and that the implementation is inside the scanned package.

---

## Circular dependency detected

If you create:

```text
A → B → A
```

Winter intentionally throws:

```text
CircularDependencyException
```

This prevents infinite recursive object creation.

---

## Snowball must have exactly one constructor

If a Snowball has multiple constructors:

```java
public CatService() {
}

public CatService(FoodService food) {
}
```

Winter cannot choose between them in v1.

Use one constructor.

---

## Proxy-related issues

JDK dynamic proxies work with interfaces.

If a class has no interface, Winter cannot expose it through the same JDK proxy mechanism.

This is one reason the example uses:

```java
CatOperations
```

and:

```java
CatService implements CatOperations
```

---

# 42. Experiments to Try

The best way to learn Winter is to break it.

## Experiment 1 — Remove `@Snowball`

Remove:

```java
@Snowball
```

from `FoodService`.

Observe what happens.

Ask yourself:

> How does Winter know the class should be managed?

---

## Experiment 2 — Add another dependency

Create:

```java
@Snowball
public class ToyService {
}
```

Then:

```java
public CatService(
        FoodService foodService,
        ToyService toyService
) {
}
```

Draw the dependency graph before running the application.

```text
CatService
   ├── FoodService
   └── ToyService
```

---

## Experiment 3 — Create a circular dependency

Create:

```text
A → B
B → A
```

Then observe:

```text
CircularDependencyException
```

---

## Experiment 4 — Remove `proceed()`

Inside an `@Around` method:

```java
return "Blocked";
```

Do not call:

```java
invocation.proceed();
```

Then observe that the real method never executes.

This is one of the best ways to understand Around advice.

---

## Experiment 5 — Change the return value

Suppose:

```java
String name() {
    return "Milo";
}
```

Then:

```java
Object result =
        invocation.proceed();

return "Snow-" + result;
```

Observe:

```text
Milo
   ↓
Snow-Milo
```

---

## Experiment 6 — Remove the proxy

Temporarily stop Winter from creating the proxy.

Then compare:

```text
direct object
```

against:

```text
proxy → handler → interceptor chain → target
```

This makes the purpose of the proxy immediately obvious.

---

## Experiment 7 — Print the runtime class

Print:

```java
cat.getClass()
```

You may see a generated proxy class rather than:

```text
CatService
```

This is a great way to see that the application is holding a proxy.

---

# 43. What to Build Next

Winter v1 intentionally stops before becoming unnecessarily complicated.

Possible future versions could introduce:

## Winter 1.1

```text
SnowballFactory
DependencyResolver
```

Separating object construction and dependency resolution from `WinterContext`.

Possible architecture:

```text
WinterContext
      │
      ├── ClassScanner
      ├── SnowballRegistry
      │
      └── SnowballFactory
              │
              └── DependencyResolver
```

---

## Winter 1.2

Improve logging architecture:

```text
WinterContext
      │
      ▼
 shared WinterLogger
      │
      ├── Context
      ├── Proxy
      └── AOP
```

This would allow a single logging configuration to control the entire framework.

---

## Winter 1.3

More powerful scanning:

```text
directories
+
JAR files
+
different class loaders
```

---

## Winter 1.4

Better dependency metadata:

```java
@Inject
```

or:

```java
@Qualifier
```

and potentially support for multiple constructors.

---

## Winter 2.0

More advanced AOP:

```text
method patterns
class patterns
annotations as pointcuts
multiple advice ordering
exception advice
```

---

# 44. Final Mental Model

If you remember only one diagram from this project, remember this:

```text
                    ┌─────────────────┐
                    │ Application     │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │ WinterContext   │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │ ClassScanner    │
                    └────────┬────────┘
                             │
                             ▼
                       @Snowball
                             │
                             ▼
                    ┌─────────────────┐
                    │ Reflection      │
                    └────────┬────────┘
                             │
                             ▼
                   Constructor params
                             │
                             ▼
                    Dependency Graph
                             │
                             ▼
                   DependencyResolver
                             │
                             ▼
                    SnowballRegistry
                             │
                             ▼
                     Real Object
                             │
                             ▼
                    @PostSnowball
                             │
                             ▼
                       JDK Proxy
                             │
                             ▼
                 WinterInvocationHandler
                             │
                             ▼
                    InterceptorChain
                             │
             ┌───────────────┼───────────────┐
             ▼               ▼               ▼
           Around          Before          After
             │               │               │
             └───────────────┼───────────────┘
                             │
                             ▼
                       Real Method
```

And the deepest conceptual flow is:

```text
DISCOVER
   ↓
INSPECT
   ↓
CREATE
   ↓
CONNECT
   ↓
MANAGE
   ↓
WRAP
   ↓
INTERCEPT
   ↓
EXECUTE
```

---

# 45. Final Cheat Sheet

## Reflection

```java
Class<?> clazz = MyClass.class;
```

Lets the framework inspect classes at runtime.

---

## Annotation

```java
@Retention(RUNTIME)
```

Keeps metadata available for runtime reflection.

---

## Scanning

```text
package
  ↓
.class files
  ↓
Class.forName()
  ↓
Class<?>
```

Discovers classes automatically.

---

## Snowball

```java
@Snowball
```

Tells Winter:

> Manage this class.

---

## IoC

```text
Application
    ↓
Winter controls object creation
```

---

## DI

```text
CatService
    ↓
FoodService
```

Winter supplies the dependency.

---

## Constructor Injection

```java
public CatService(
        FoodService foodService
)
```

The constructor describes the dependency graph.

---

## Interface DI

```text
CatOperations
      ↓
CatService
```

Winter resolves the interface to an implementation.

---

## Registry

```text
Class → Object
```

Stores managed Snowballs.

---

## Singleton

```text
request
   ↓
registry
   ↓
existing object
```

One managed instance per context.

---

## Circular Dependency

```text
A → B → A
```

Detected before infinite recursion.

---

## Lifecycle

```text
create
  ↓
@PostSnowball
  ↓
ready
```

---

## Proxy

```text
caller
  ↓
proxy
  ↓
handler
  ↓
target
```

Adds a controllable layer around the real object.

---

## InvocationHandler

```java
invoke(...)
```

Receives method calls made through the proxy.

---

## AOP

```text
business method
       +
cross-cutting behavior
```

---

## Before

```text
Before
  ↓
method
```

---

## After

```text
method
  ↓
After
```

---

## Around

```text
Around Before
      ↓
   proceed()
      ↓
Around After
```

But:

```java
// no proceed()
```

means:

```text
real method never executes
```

---

## MethodInvocation

Contains:

```text
target
method
args
chain
```

and exposes:

```java
proceed()
```

---

## Interceptor Chain

```text
Interceptor 1
      ↓
Interceptor 2
      ↓
Interceptor 3
      ↓
Real Method
```

---

# The Core Lesson

Winter is small.

But the ideas behind it are not.

A framework does not need to feel magical.

Once you reduce it to the underlying mechanisms, a large portion of the magic becomes understandable:

```text
Annotations
     +
Reflection
     +
Class Scanning
     +
Object Creation
     +
Dependency Graph
     +
Registry
     +
Proxy
     +
Interceptors
```

And suddenly this:

```java
cat.meow();
```

is no longer just:

```text
"call a method"
```

It can be understood as:

```text
cat
 ↓
proxy
 ↓
InvocationHandler
 ↓
MethodInvocation
 ↓
InterceptorChain
 ↓
Around
 ↓
Before
 ↓
After
 ↓
real CatService
 ↓
FoodService
```

That is the main reason Winter exists.

---

## Winter

**Winter Framework** is an educational Java project built from scratch to explore the internal mechanisms behind dependency injection containers, proxies, and AOP systems.

It is intentionally small.

It is intentionally imperfect. *(Well... not so intentionally.)*

And that is the point.

```text
          WINTER

    Learn the framework
           by
    building the framework.
```

---

**Author:** Amirhossein Omidi  
**Contact:** 65mirhossein@gmail.com
