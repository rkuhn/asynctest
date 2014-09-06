# AsyncTest

Testing asynchronous code requires the implementor to jump through hoops to fit things into the traditional synchronous unit-testing corset. It should be possible to write test cases that return Futures for their results and which are then inspected asynchronously by the test framework.

## Rough Idea

The process should be:

* spawn off test procedures wrapped in Future (asynchronously) to get them all off the ground as fast as possible
* test cases that return a Future will be flattened, meaning that the resulting Future holds the asynchronous value that is produced by the test case; the value itself is irrelevant, only that it is not a failure counts
* each test case Future is bounded by a timeout and recovered wrap failures in a dedicated ADT
* all resulting Futures are sequenced into the overall test suite result; no further timeout is necessary assuming that the sequencing operation is reliable

This is in a first rough cut attached to a JUnit Runner, which means that the runner will synchronously await the completion of all these Futures and feed the results back into the RunNotifier. Since this means spending one main thread per test suite this is not optimal and will later be replaced by a purely asynchronous mechanism.

## Contributing

I am very much interested in feedback and suggestions, please open issues. Concerning code contributions I’ll have to figure out the CLA mechanism first.

## License

This code is open source software licensed under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).