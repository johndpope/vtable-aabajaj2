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


void main(...){
    int x;
    x=1;
    printf("%d\n",);
}