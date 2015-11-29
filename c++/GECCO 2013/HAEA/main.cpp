#include <iostream>
#include <boost/random.hpp>
#include <boost/random/random_device.hpp>
#include <boost/random/normal_distribution.hpp>
#include <boost/random/uniform_int.hpp>
#include <boost/random/uniform_real.hpp>
#include <boost/random/variate_generator.hpp>
#include <boost/generator_iterator.hpp>
#include <boost/random/linear_congruential.hpp>
#include <limits>
#include <cmath>
#include <omp.h>
#include <unistd.h>
#include <string>
#include <stdio.h>
#include <array>
#include <algorithm>
#include <vector>
#include <random>
#include <functional>

#include "Benchmarks.h"
#include "F1.h"
#include "F2.h"
#include "F3.h"
#include "F4.h"
#include "F5.h"
#include "F6.h"
#include "F7.h"
#include "F8.h"
#include "F9.h"
#include "F10.h"
#include "F11.h"
#include "F12.h"
#include "F13.h"
#include "F14.h"
#include "F15.h"

#define DIM 1000
#define MIN 0
#define MAX 1
#define GENETIC_OPERATORS 4
#define RANDOM_POINTS 2
#define MOMENTUM_POINTS 1
#define PERCENTAGE_SPACE 0.05
#define MAX_UPDATES 1
#define MOMENTUM_SCALE 2.0

using namespace std;

typedef boost::mt19937 base_generator_type;

Benchmarks *f[16];// = new Benchmarks[16];
double bounds[16][2];

// all necessary functions

void init() {
    f[1] = new F1();
    f[2] = new F2();
    f[3] = new F3();
    f[4] = new F4();
    f[5] = new F5();
    f[6] = new F6();
    f[7] = new F7();
    f[8] = new F8();
    f[9] = new F9();
    f[10] = new F10();
    f[11] = new F11();
    f[12] = new F12();
    f[13] = new F13();
    f[14] = new F14();
    f[15] = new F15();

    for (int i = 1; i < 16; ++i) {
        bounds[i][MIN] = f[i]->getMinX();
        bounds[i][MAX] = f[i]->getMaxX();
    }
}

class Individual {
public:
    double x[DIM];
    double momentumVector[DIM];
    double rates[GENETIC_OPERATORS];
    double currentSum[DIM];
    int updates;

    /*
     * Assuming vector from 0 to point
     */
    void updateMomentumVector(double *point) {
        for(int i = 0; i < DIM; ++i) {
            currentSum[i] = point[i] - x[i];
        }

        ++updates;

        // update momentum vector
        if(updates == MAX_UPDATES) {
            //cout << "updating" << endl;
            // normalize
            double sumsq = 0.0;
            for(int i = 0; i < DIM; ++i) {
                //cout << currentSum[i] << " ";
                sumsq += currentSum[i] * currentSum[i];
            }
            //cout << endl;

            sumsq = sqrt(sumsq);

            for(int i = 0; i < DIM; ++i) {
                currentSum[i] /= sumsq;
            }

            // update
            memcpy(momentumVector, currentSum, DIM * sizeof(double));
            memset(currentSum, 0, DIM * sizeof(double));

            updates = 0;
        }
    }

    Individual(){
        updates = 0;
        memset(currentSum, 0, sizeof(double) * DIM);
    }
    ~Individual(){ }
};

// Operator interfaces
class Operator {
public:
    int arguments;
    virtual vector<array<double, DIM>> apply(Individual *args, base_generator_type eng, double* bounds) = 0;
};


class ParetoMutation : public Operator {
    double paretoRandomNumber() {
        double alpha = 2.0, r;
        r = uniformRand() / 2.0; // rand() / rand_max + 1
        return 1.0 / pow(r, 1.0/alpha);
    }

public:
    ParetoMutation(base_generator_type eng) :
            unifDist(0, 1),
            uniformRand(eng, unifDist)
    {
        arguments = 1;
    }

    vector<array<double, DIM>> apply(Individual *args, base_generator_type eng, double* bounds) {
        Individual toMutate = args[0];

        vector<array<double, DIM>> toReturn(RANDOM_POINTS + MOMENTUM_POINTS);
        double prob = (30 / DIM) + 0.1;
        int i;
        double scale = (abs(bounds[0]) + abs(bounds[1])) * PERCENTAGE_SPACE;

        for(i = 0; i < RANDOM_POINTS; ++i) {
            for (int j = 0; j < DIM; ++j) {
                if(uniformRand() < prob) {
                    toReturn[i][j] =  toMutate.x[j] + (uniformRand() < 0.5 ? -1 : 1) * paretoRandomNumber() * scale;
                } else {
                    toReturn[i][j] =  toMutate.x[j];
                }
            }
        }

        for(i; i < RANDOM_POINTS + MOMENTUM_POINTS; ++i) {
            double momentumVariable = toMutate.momentumVector[i] * scale * MOMENTUM_SCALE; // go far

            for (int j = 0; j < DIM; ++j) {
                if(uniformRand() < prob) {
                    toReturn[i][j] =  toMutate.x[j] + momentumVariable + (uniformRand() < 0.5 ? -1 : 1) * paretoRandomNumber() * scale;
                } else {
                    toReturn[i][j] =  toMutate.x[j] + momentumVariable;
                }
            }
        }

        return toReturn;
    }

private:
    boost::uniform_real<> unifDist;
    boost::variate_generator<base_generator_type&, boost::uniform_real<> > uniformRand;
};

class GaussianMutation : public Operator {
public:
    GaussianMutation(base_generator_type eng) :
            dist(0, 1), unifDist(0, 1),
            normalRand(eng, dist), uniformRand(eng, unifDist)
    {
        arguments = 1;
    }

    vector<array<double, DIM>> apply(Individual *args, base_generator_type eng, double* bounds) {
        //cout << "gaussian" << endl;
        Individual toMutate = args[0];

        vector<array<double, DIM>> toReturn(RANDOM_POINTS + MOMENTUM_POINTS);
        double prob = (30 / DIM) + 0.1;
        int i;
        double scale = (abs(bounds[0]) + abs(bounds[1])) * PERCENTAGE_SPACE;

        for(i = 0; i < RANDOM_POINTS; ++i) {
            for (int j = 0; j < DIM; ++j) {
                double rand = uniformRand();
                //cout << "rand: " << rand << endl;
                if(rand < prob) {
                    toReturn[i][j] =  toMutate.x[j] + normalRand() * scale;
                    //cout << "changed" << endl;
                }
                else {
                    toReturn[i][j] =  toMutate.x[j];
                }
            }
        }

        for(i; i < RANDOM_POINTS + MOMENTUM_POINTS; ++i) {
            double momentumVariable = toMutate.momentumVector[i] * scale * MOMENTUM_SCALE; // go far

            for (int j = 0; j < DIM; ++j) {
                if(uniformRand() < prob) {
                    toReturn[i][j] =  toMutate.x[j] + momentumVariable + normalRand() * scale;
                    //cout << "changed" << endl;
                } else {
                    toReturn[i][j] =  toMutate.x[j] + momentumVariable;
                }
            }
        }

        return toReturn;
    }

private:
    boost::normal_distribution<> dist;
    boost::uniform_real<> unifDist;
    boost::variate_generator<base_generator_type&, boost::normal_distribution<> > normalRand;
    boost::variate_generator<base_generator_type&, boost::uniform_real<> > uniformRand;
};

class LinearRandomXOver : public Operator {
public:
    LinearRandomXOver(base_generator_type eng) :
            uniformDist(0, 1),
            uniformRand(eng, uniformDist)
    {
        arguments = 2;
    }

    vector<array<double, DIM>> apply(Individual *args, base_generator_type eng, double* bounds) {
        vector<array<double, DIM>> toReturn(arguments);

        double c = uniformRand();
        double c_1 = 1.0 - c;
        for (int i = 0; i < DIM; ++i) {
            toReturn[0][i] = c * args[0].x[i] + c_1 * args[1].x[i];
            toReturn[1][i] = c_1 * args[0].x[i] + c * args[1].x[i];
        }

        return toReturn;
    }

private:
    boost::uniform_real<> uniformDist;
    boost::variate_generator<base_generator_type&, boost::uniform_real<> > uniformRand;
};

class RandomXOver : public Operator {
public:
    RandomXOver(base_generator_type eng) :
            uniformDist(0, 1),
            uniformRand(eng, uniformDist)

    {
        arguments = 2;
    }

    vector<array<double, DIM>> apply(Individual *args, base_generator_type eng, double *bounds) {

        vector<array<double, DIM>> toReturn(arguments);
        for (int i = 0; i < DIM; ++i) {
            if(uniformRand() < 0.5) {
                toReturn[0][i] = args[1].x[i];
                toReturn[1][i] = args[0].x[i];
            } else {
                toReturn[0][i] = args[0].x[i];
                toReturn[1][i] = args[1].x[i];
            }
        }

        return toReturn;
    }

private:
    boost::uniform_real<> uniformDist;
    boost::variate_generator<base_generator_type&, boost::uniform_real<> > uniformRand;
};

/*
class GaussianMutation : public Operator {
public:
    GaussianMutation(base_generator_type eng) :
            dist(0, 1), unifDist(0, 1),
            normalRand(eng, dist), uniformRand(eng, unifDist)

    {
        arguments = 1;
    }

    vector<array<double, DIM>> apply(Individual *args, base_generator_type eng) {

        vector<array<double, DIM>> toReturn(arguments);
        double prob = (1.0 / DIM) + 0.15;
        for (int i = 0; i < DIM; ++i) {
            if(uniformRand() < prob)
                toReturn[0][i] = args->x[i] + normalRand();
            else
                toReturn[0][i] =  args->x[i];
        }

        return toReturn;
    }

private:
    boost::normal_distribution<> dist;
    boost::uniform_real<> unifDist;
    boost::variate_generator<base_generator_type&, boost::normal_distribution<> > normalRand;
    boost::variate_generator<base_generator_type&, boost::uniform_real<> > uniformRand;

};

class BinaryMutation : public Operator {
public:
    BinaryMutation(base_generator_type eng) :
            unifInt(0, 51), unifDist(0, 1),
            uniformInt(eng, unifInt), uniformRand(eng, unifDist)
    {
        arguments = 1;
    }

    vector<array<double, DIM>> apply(Individual *args, base_generator_type eng) {
        vector<array<double, DIM>> toReturn(arguments);

        double prob = (1.0 / DIM) + 0.05;
        for (int i = 0; i < DIM; ++i) {
            if(uniformRand() < prob) {
                int bit = uniformInt();
                toReturn[0][i] = (double)((int)(args->x[i]) ^ (1u << bit));
            } else {
                toReturn[0][i] = args->x[i];
            }
        }

        return toReturn;
    }
private:
    boost::uniform_int<> unifInt;
    boost::uniform_real<> unifDist;
    boost::variate_generator<base_generator_type&, boost::uniform_int<> > uniformInt;
    boost::variate_generator<base_generator_type&, boost::uniform_real<> > uniformRand;
};

class XOver : public Operator {
public:
    XOver(base_generator_type eng) :
            uniformIntDist(0, DIM),
            uniformRand(eng, uniformIntDist)

    {
        arguments = 2;
    }

    vector<array<double, DIM>> apply(Individual *args, base_generator_type eng)
    {

        vector<array<double, DIM>> toReturn(arguments);
        int cross = uniformRand();
        for (int i = 0; i < DIM; ++i) {
            if(i < cross) {
                toReturn[0][i] = args[0].x[i];
                toReturn[1][i] = args[1].x[i];
            } else {
                toReturn[0][i] = args[1].x[i];
                toReturn[1][i] = args[0].x[i];
            }

        }

        return toReturn;
    }

private:
    boost::uniform_int<> uniformIntDist;
    boost::variate_generator<base_generator_type&, boost::uniform_int<> > uniformRand;
};
*/

class Distance {
public:
    static double Tanimoto(Individual a, Individual b) {
        double p, q, m;
        p = q = m = 0.0;
        for (int i = 0; i < DIM; ++i) {
            p += a.x[i];
            q += b.x[i];
            m += min(a.x[i], b.x[i]);
        }

        return (p + q - 2 * m) / (p + q - m);
    }

    static double Intersection(Individual a, Individual b) {
        double ans = 0.0;
        for (int i = 0; i < DIM; ++i) {
            ans += min(a.x[i], b.x[i]);
        }
        return 1 - ans;
    }
};

void ratesNormalize(Individual* ind) {
    double sum = 0;
    for (int i = 0; i < GENETIC_OPERATORS; ++i) {
        sum += ind->rates[i];
    }
    for (int i = 0; i < GENETIC_OPERATORS; ++i) {
        ind->rates[i] /= sum;
    }

    sum = 0.0;

    for (int j = 0; j < GENETIC_OPERATORS; ++j) {
        sum += ind->rates[j];
    }
}

int operatorSelect(double *rates, boost::variate_generator<base_generator_type&, boost::uniform_real<> > random) {
    double values[GENETIC_OPERATORS + 2];
    double sum = 0.0;
    values[0] = 0.0;
    values[GENETIC_OPERATORS + 1] = 1.0;
    for (int i = 0; i < GENETIC_OPERATORS; ++i) {
        values[i + 1] = sum;
        sum += rates[i];
    }

    double num = random();

    for (int j = 0; j < GENETIC_OPERATORS + 1; ++j) {
        if(values[j] < num && num < values[j + 1]) {
            return j - 1;
        }
    }
}

Individual* initPopulation(int lambda, boost::variate_generator<base_generator_type&, boost::uniform_real<> > uniformReal,
                           boost::variate_generator<base_generator_type&, boost::uniform_real<> > uniformRates) {

    Individual *pop = new Individual[lambda];

    for (int i = 0; i < lambda; ++i) {

        for (int j = 0; j < DIM; ++j) {
            pop[i].x[j] = uniformReal();
        }

        for (int k = 0; k < GENETIC_OPERATORS; ++k) {
            pop[i].rates[k] = uniformRates();
        }
        ratesNormalize(pop + i);
    }

    return pop;
}

double bestIndividual(Individual *population, Benchmarks* func, int lambda) {
    double best = numeric_limits<double>::max();
    for (int i = 0; i < lambda; ++i) {
        double fitness = func->compute(population[i].x);
        if(fitness < best) {
            best = fitness;
        }
    }

    return best;
}

void savePopulation(Individual *pop, int lambda, int dim, string save) {
    FILE *f = fopen(save.c_str(), "w");
    for (int i = 0; i < lambda; ++i) {
        for (int j = 0; j < dim; ++j) {
            fprintf(f, "%lf", pop[i].x[j]);
            if(j != dim - 1)
                fprintf(f, ",");
        }
        fprintf(f, "\n");
    }
}

vector<double> HAEA(Operator *operators[GENETIC_OPERATORS], int lambda, int currFunction, base_generator_type eng, int iterations, int id) {
    vector<double> toReturn(3);
    bool measure[2];
    measure[0] = true;
    measure[1] = true;
    string saveFile = "./data/f" + to_string(currFunction) + "p" + to_string(id);
    ssize_t t;

    // delta & operator selection random generator
    boost::uniform_real<> deltaDist(0, 1);
    boost::variate_generator<base_generator_type&, boost::uniform_real<> > uniformDelta(eng, deltaDist);

    boost::uniform_real<> randomSpaceSize(bounds[currFunction][MIN], bounds[currFunction][MAX]);
    boost::variate_generator<base_generator_type&, boost::uniform_real<> > randomSpace(eng, randomSpaceSize);

    Benchmarks* function = f[currFunction];

    Individual *population = initPopulation(lambda, randomSpace, uniformDelta);
    int i = 0;
    while(i < iterations) {
        for (int j = 0; j < lambda; ++j) {
            // extracting rates
            double *rates = population[j].rates;

            double delta = uniformDelta();
            int operatorIndex = operatorSelect(rates, uniformDelta);
            Operator *op = operators[operatorIndex];
            int args = op->arguments;
            Individual selected[2];

            double parentFitness = function->compute(population[j].x);

            selected[0] = population[j];
            if(args == 2) {
                double best = numeric_limits<double>::max();
                int index = 0;
                for (int k = 0; k < lambda; ++k) {
                    if(k == j) continue;
                    double current = Distance::Intersection(selected[0], population[k]);
                    if(current < best) {
                        best = current;
                        index = k;
                    }
                }
                selected[1] = population[index];
            }

            vector< array<double, DIM> > res = op->apply(selected, eng, (double *) (bounds + currFunction * 2));

            //cout << "res size: " << res.size() << endl;

            double *offspring = new double[res.size() * DIM];
            for (int k = 0; k < res.size(); ++k) {
                copy(begin(res[k]), end(res[k]), offspring + k*DIM);
            }

            /*if(j == 0) {
                for(int k = 0; k < res.size(); ++k) {
                    for(int a = 0; a < DIM; a++) {
                        cout << offspring[k*DIM + a] << " ";
                    }
                    cout << endl;
                }
                cout << endl;
            }*/

            double *child;
            double childFitness;

            if(op->arguments > 1) {
                double child1 = function->compute(offspring);
                double child2 = function->compute(offspring + DIM);

                if(child1 < child2) {
                    child = offspring;
                    childFitness = child1;
                } else {
                    child = offspring + DIM;
                    childFitness = child2;
                }

                i += 2;
            } else {
                childFitness = numeric_limits<double>::max();

                for(int currentChild = 0; currentChild < res.size(); ++currentChild) {

                    // fixing operation
                    for (int k = 0; k < DIM; ++k) {
                        if(offspring[currentChild * DIM + k] > bounds[currFunction][MAX]) {
                            offspring[currentChild * DIM + k] = bounds[currFunction][MAX];
                        } else if(offspring[currentChild * DIM + k] < bounds[currFunction][MIN]) {
                            offspring[currentChild * DIM + k] = bounds[currFunction][MIN];
                        }
                    }

                    double fitness = function->compute(offspring + currentChild * DIM);
                    /*if(j == 0) {
                        cout << "child " << currentChild << " fitness: " << fitness << endl;
                    }*/
                    if(fitness < childFitness) {
                        childFitness = fitness;
                        child = offspring + currentChild * DIM;
                    }
                }

                //
                i += res.size();
            }

            /*if(j == 0) {
                cout << "operator: " << operatorIndex << endl;
                for(int z = 0 ; z < DIM; ++z) {
                    cout << population[j].x[z] << " ";
                }
                cout << endl;
            }*/

            if(childFitness <= parentFitness) {
                if(childFitness < parentFitness) {
                    population[j].rates[operatorIndex] *= (1.0 + delta);
                }
                population[j].updateMomentumVector(child);
                memcpy(&(population[j].x), child, DIM * sizeof(double));
                /*if(j == 0) {
                    cout << "good" << endl;
                }*/
            } else {
                population[j].rates[operatorIndex] *= (1.0 - delta);
                /*if(j == 0) {
                    cout << "bad" << endl;
                }*/
            }

            /*if(j == 0) {
                cout << "parent fitness: " << parentFitness << endl;
                cout << "child fitness: " << childFitness << endl;
            }*/

            delete offspring;

            ratesNormalize(population + j);

            // parent iteration
            i++;
        }

        //cout << i << endl;

        if(i >= (int)(1.2e5) && measure[0]) {
            toReturn[0] = bestIndividual(population, function, lambda);
            /*if(id == 1)
                cout << toReturn[0] << endl;*/
            measure[0] = false;

            //savePopulation(population, lambda, DIM, saveFile);
        } else if(i >= (int)(6e5) && measure[1]) {
            toReturn[1] = bestIndividual(population, function, lambda);
            /*if(id == 1)
                cout << toReturn[1] << endl;*/
            measure[1] = false;
            //savePopulation(population, lambda, DIM, saveFile);
        }
    }

    toReturn[2] = bestIndividual(population, function, lambda);
    savePopulation(population, lambda, DIM, saveFile);

    /*if(id == 1)
        cout << "best: " << toReturn[2] << endl;*/

    return toReturn;
}

void saveFile(string s, double arr[][3]) {
    FILE *f = fopen(s.c_str(), "w");
    for (int i = 0; i < 25; ++i) {
        for (int j = 0; j < 3; ++j) {
            fprintf(f, "%lf", arr[i][j]);
            if(j != 2)
                fprintf(f, ",");
        }
        fprintf(f, "\n");
    }
}

#define EVAL 6

int main() {
    init();

    random_device rd;
    int descriptor[2];
    pipe(descriptor);
    pid_t pid;

    for (int k = 1; k <= 1; ++k) {
        double best[EVAL][3];

        for (int i = 0; i < EVAL; ++i) {
            pid = fork();

            if(pid != 0) {
                continue;
            } else {
                close(descriptor[0]);

                double res[3];
                base_generator_type eng( rd() );

                Operator **operators = new Operator*[GENETIC_OPERATORS];

                operators[0] = new GaussianMutation(eng);
                operators[1] = new ParetoMutation(eng);
                operators[2] = new LinearRandomXOver(eng);
                operators[3] = new RandomXOver(eng);

                vector<double> result = HAEA(operators, 30, k, eng, (int) 2e6, i);

                cout << result[0] << "," << result[1] << "," << result[2] << endl;

                res[0] = result[0];
                res[1] = result[1];
                res[2] = result[2];

                write(descriptor[1], res, sizeof(double) * 3);

                for (int j = 0; j < GENETIC_OPERATORS; ++j) {
                    delete operators[j];
                }
                delete [] operators;

                close(descriptor[1]);

                exit(0);
            }
        }

        for(int i = 0; i < EVAL; ++i) {
            read(descriptor[0], best + i, sizeof(double) * 3);
        }

        saveFile("f" + to_string(k) + ".txt", best);
        cout << "finish" << endl;
    }

    close(descriptor[0]);
    close(descriptor[1]);

    return 0;
}