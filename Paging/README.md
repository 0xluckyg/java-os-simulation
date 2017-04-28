To compile:
javac Main

To run:
java Main M P S J N R
M, the machine size in words.
P, the page size in words.
S, the size of a process, i.e., the references are to virtual addresses 0..S-1.
J, the ‘‘job mix’’, which determines A, B, and C, as described below.
N, the number of references for each process.
R, the replacement algorithm, FIFO, RANDOM, or LRU.

ex: java Main 10 10 20 1 10 lru 0
7th argument, the debugging out put flag, is not supported.
