Translated a subset of Java to pure C using ANTLR, StringTemplate, and JAVA.
The subset is focused on classes and methods. Language translation, polymorphism and dynamic dispatch is implemented using vtables, 
which C++ uses.

Eg: 
`-------------------------------------------------JAVA CODE------------------------------------------------------------------------------ 

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
class Coder extends Employee {
    Mgr boss;
}
class Pro extends Coder {
  Coder getCoder() {
      Coder c;
      c = new Coder();
      c.boss = new Mgr();
      c.boss.other = new Employee();
      return c;
    }
}

Pro pro;
pro = new Pro();

pro.boss = new Mgr();
pro.boss.other = new Employee();
pro.boss.other.number = pro.getCoder().boss.other.getID();

printf("%d\n", pro.boss.other.number);
printf("%d\n", pro.boss.getID());
printf("%d\n", pro.boss.something());
  
-----------------------------------CONVERTED C CODE-------------------------------------------------------------------------------------
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

// D e f i n e  C l a s s  Coder
typedef struct {
    metadata *clazz;
    int number;
    Mgr * boss;
} Coder;

#define Coder_getID_SLOT 0
#define Coder_something_SLOT 1



void (*Coder_vtable[])() = {
    (void (*)())&Employee_getID,
    (void (*)())&Employee_something
};

metadata Coder_metadata = {"Coder", sizeof(Coder), &Coder_vtable};

// D e f i n e  C l a s s  Pro
typedef struct {
    metadata *clazz;
    int number;
    Mgr * boss;
} Pro;

#define Pro_getID_SLOT 0
#define Pro_something_SLOT 1
#define Pro_getCoder_SLOT 2


Coder * Pro_getCoder(Pro *this)
{
    Coder * c;

    c = ((Coder *)alloc(&Coder_metadata));
    c->boss = ((Mgr *)alloc(&Mgr_metadata));
    c->boss->other = ((Employee *)alloc(&Employee_metadata));
    return c;
}

void (*Pro_vtable[])() = {
    (void (*)())&Employee_getID,
    (void (*)())&Employee_something,
    (void (*)())&Pro_getCoder
};

metadata Pro_metadata = {"Pro", sizeof(Pro), &Pro_vtable};

int main(int argc, char *argv[])
{
    Pro * pro;

    pro = ((Pro *)alloc(&Pro_metadata));
    pro->boss = ((Mgr *)alloc(&Mgr_metadata));
    pro->boss->other = ((Employee *)alloc(&Employee_metadata));
    pro->boss->other->number = (*(int (*)(Employee *))(*((*(Coder * (*)(Pro *))(*(pro)->clazz->_vtable)[Pro_getCoder_SLOT])(((Pro *)pro))->boss->other)->clazz->_vtable)[Employee_getID_SLOT])(((Employee *)(*(Coder * (*)(Pro *))(*(pro)->clazz->_vtable)[Pro_getCoder_SLOT])(((Pro *)pro))->boss->other));
    printf("%d\n", pro->boss->other->number);
    printf("%d\n", (*(int (*)(Employee *))(*(pro->boss)->clazz->_vtable)[Mgr_getID_SLOT])(((Employee *)pro->boss)));
    printf("%d\n", (*(int (*)(Employee *))(*(pro->boss)->clazz->_vtable)[Mgr_something_SLOT])(((Employee *)pro->boss)));
    return 0;
}
`
