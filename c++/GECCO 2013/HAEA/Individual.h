//
// Created by jefferson on 23/10/15.
//

#ifndef GENETICALGORITHM_INDIVIDUAL_H
#define GENETICALGORITHM_INDIVIDUAL_H
#define DIM 1000
#define GENETIC_OPERATORS 6


class Individual {
public:
    double x[1000];
    double rates[GENETIC_OPERATORS];
};


#endif //GENETICALGORITHM_INDIVIDUAL_H
