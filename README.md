## JAVA TO C TRANSLATOR
Translated a subset of Java to pure C using ANTLR, StringTemplate, and JAVA.
The subset is focused on classes and methods. Language translation, polymorphism and dynamic dispatch is implemented using vtables, 
which C++ uses.

### JAVA Class Employee and Manager will be converted to C as follows: 

```
class Employee {
    int number;
    int getID() { return 123; }
    int something() {
      this.getID();
      return 99;
    }
}
class Mgr extends Employee {
    int level;
    Employee other;
}
```

C code: 

```
#include <stdio.h>
#include <stdlib.h>

typedef struct {
    char *name;
    int size;
    void (*(*_vtable)[])();
} metadata;

typedef struct {
    metadata *clazz;
} object;

object *alloc(metadata *clazz) {
    object *p = calloc(1, clazz->size); // wipe to 0s
    p->clazz = clazz;
    return p;
}



// D e f i n e  C l a s s  Employee
typedef struct {
    metadata *clazz;
    int number;
} Employee;

#define Employee_getID_SLOT 0
#define Employee_something_SLOT 1


int Employee_getID(Employee *this)
{
    return 123;
}
int Employee_something(Employee *this)
{
    (*(int (*)(Employee *))(*(this)->clazz->_vtable)[Employee_getID_SLOT])(((Employee *)this));
    return 99;
}

void (*Employee_vtable[])() = {
    (void (*)())&Employee_getID,
    (void (*)())&Employee_something
};

metadata Employee_metadata = {"Employee", sizeof(Employee), &Employee_vtable};

// D e f i n e  C l a s s  Mgr
typedef struct {
    metadata *clazz;
    int number;
    int level;
    Employee * other;
} Mgr;

#define Mgr_getID_SLOT 0
#define Mgr_something_SLOT 1



void (*Mgr_vtable[])() = {
    (void (*)())&Employee_getID,
    (void (*)())&Employee_something
};

metadata Mgr_metadata = {"Mgr", sizeof(Mgr), &Mgr_vtable};
```
