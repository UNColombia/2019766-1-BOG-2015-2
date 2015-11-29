//
// Created by jefferson on 23/10/15.
//

#ifndef GENETICALGORITHM_DISTANCE_H
#define GENETICALGORITHM_DISTANCE_H

#include "Individual.h"

class Distance {
public:
    static double Tanimoto(Individual a, Individual b);
    static double Intersection(Individual a, Individual b);
};


#endif //GENETICALGORITHM_DISTANCE_H
