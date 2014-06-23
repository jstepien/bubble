# Bubble

Lightweight containers for Clojure namespaces.
Atomic reloading and disaster recovery included.

[![Build Status](https://travis-ci.org/jstepien/bubble.svg)](https://travis-ci.org/jstepien/bubble)

## High-level usage overview

Create a new bubble by calling `bubble.core/init`.
The returned bubble is empty.

           ┌────────────────────┐
           │                    │
           │                    │
           │       bubble       │
           └────────────────────┘

In order to fill it with your namespaces use `bubble.core/blow`.
The function takes two obligatory arguments: the bubble itself and a collection
of sources of namespaces you want to load.
The code which you pass is compiled into a container in the bubble.

           ┌────────────────────┐
           │ ┌───────┐          │
           │ │ns1 ns2│          │
           │ └───────┘          │
           │       bubble       │
           └────────────────────┘

Instead of calling `bubble.core/blow` directly you can use the `lein-bubble`
plugin available in the `lein-plugin` directory.
It allows you to remotely inject code into a bubble over nREPL.

You cannot call code loaded in a bubble directly;
in order to access vars inside you have to use `bubble.core/through`.
Expected arguments are a bubble and a qualified symbol of the var you want to
retrieve.
Think of it as a lens _through_ which you can look into a bubble.


           ┌────────────────────┐
           │ ┌───────┐          │
    through → ns1 ns2│          │
           │ └───────┘          │
           │       bubble       │
           └────────────────────┘

Code in a bubble can be safely reloaded.
In order to guarantee that the new version will not interfere with existing code
the previously built container remains unaltered.
`bubble.core/blow` puts new code in a separate container.

           ┌────────────────────┐
           │ ┌───────┐┌───────┐ │
    through → ns1 ns2││ns1 ns2│ │
           │ └───────┘└───────┘ │
           │       bubble       │
           └────────────────────┘

In absence of errors `bubble.core/through` invoked on the bubble will return
vars from the new container.
The old container will be disposed of.

           ┌────────────────────┐
           │          ┌───────┐ │
           │          │ns1 ns2 ← through
           │          └───────┘ │
           │       bubble       │
           └────────────────────┘

On the other hand, any errors which happen during reloading will cause the newly
build container to be deleted.
The old one will remain unchanged.

`bubble.core/blow` accepts two optional named arguments: `:before` and `:after`.
`:before` is invoked once the new container is successfully loaded but before
the old one is replaced.
It can be used to perform arbitrary actions in newly compiled namespaces, e.g.
opening listening sockets, initialising connections to remote services or
running basic sanity checks.
Any exception thrown from `:before` will cancel the reload, remove new code and
leave the bubble in the state from before invocation of `bubble.core/blow`.

`:after` is called once the container is being removed after next successful
invocation of `bubble.core/blow` on this bubble.
It is supposed to clean up everything what the `:before` function had initialised.
Exceptions thrown from this function are ignored.

Both `:before` and `:after` receive a single argument: an unary function through
which vars in newly loaded and currently unloaded namespaces respectively can be
accessed.

Take a look inside the `examples` directory to see how Bubble can be used in
practice in order to achieve pauseless and atomic reloading of Clojure sources.

## License

    Copyright (c) 2014 Jan Stępień

    Permission is hereby granted, free of charge, to any person
    obtaining a copy of this software and associated documentation
    files (the "Software"), to deal in the Software without
    restriction, including without limitation the rights to use,
    copy, modify, merge, publish, distribute, sublicense, and/or
    sell copies of the Software, and to permit persons to whom the
    Software is furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included
    in all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
    OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
    THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
    FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
    DEALINGS IN THE SOFTWARE.
