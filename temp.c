/* clock example: frequency of primes */
#include <stdio.h>      /* printf */
#include <time.h>       /* clock_t, clock, CLOCKS_PER_SEC */
#include <math.h>       /* sqrt */


int main ()
{
	  clock_t t;
	    int f;
	      t = clock();
		      t = clock() - t;
		        printf ("It took me %d clicks (%f seconds).\n",t,((float)t)/CLOCKS_PER_SEC);
			  return 0;
}
