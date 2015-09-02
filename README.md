# Expr

By Tim Henderson

## What?

A simple expression calculator for integer math. You give it an expression:

    $ expr '1 + 2'
    (+ 1 2)
    3

It will calculate the result and additionally give you back your expression as
an s-expression.

## Why?

To demonstrate gradle, testing, lexing and recursive descent parsing.


## Install

You must have java 1.7+ and git on your system. Get those first!

    $ git clone https://github.com/hacsoc/expr.git
    ## some output
    $ cd expr
    $ ./gradlew test installApp
    ## bunch of output
    $ ./build/install/expr/bin/expr '1+2'
    (+ 1 2)
    3

To make it availabl for this session run

    $ export PATH=$(pwd)/build/install/expr/bin:$PATH

Now you can just run

    $ expr '4 * 3/5'
    (/ (* 4 3) 5)
    2

To "install" permanently do

    $ echo "export PATH=$(pwd)/build/install/expr/bin:$PATH" >> .bashrc


