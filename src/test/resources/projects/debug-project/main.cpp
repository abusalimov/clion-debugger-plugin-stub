// FILE_MAIN
#include <iostream>

static int callme() {
    return 0;  // LINE_CALLME
}

static int fun() {
    callme();  // LINE_FUN
    return 0;
}

int main(int argc, const char *argv[]) {
    for (int i = 1; i < argc; i++) {
        if (strcmp("fun", argv[i]) == 0) {
            fun();
        }
    }
    return 0;
}
