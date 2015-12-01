import numpy as np
from math import cos

H = np.float32(1e-6)

def hessian(fun, point, args):
	f1 = gradient(fun, point, args)
	n = f1.size
	hess = np.zeros((n, n))
	for i in xrange(n):
		current_value = point[i]
		point[i] = current_value + H
		f2 = gradient(fun, point, args)
		hess[:, i] = (f2 - f1) / H
		point[i] = current_value
	return hess


def gradient(fun, point, args):
	gradient_vector = np.array([0.0 for i in xrange(point.size)])
	for i in xrange(point.size):
		point[i] -= H
		point_min = fun(point, args)
		point[i] += 2 * H
		point_max = fun(point, args)
		point[i] -= H
		gradient_vector[i] = (point_max - point_min) / (2 * H)

	return gradient_vector

def gradient_descent(fun, initial_point, learning_rate, momentum, args):
	current_point = initial_point if not (isinstance(initial_point, float) or isinstance(initial_point, int)) else np.array([initial_point])
	if not type(current_point) == np.ndarray:
		current_point = np.array(initial_point)

	momentum_vector = np.array([0.0 for i in xrange(current_point.size)])
	while np.sum(gradient(fun, current_point, args)) > 1e-5:
		temp = current_point
		current_point -= learning_rate * (gradient(fun, current_point, args) / np.linalg.det(hessian(fun, current_point, args))) + \
						momentum * (gradient(fun, momentum_vector, args))
		momentum_vector = temp

	return current_point if current_point.size > 1 else current_point[0]

def fun1(x):
	return (x[0] + 1.0) ** 2 + (x[1] + 1.0) ** 2

def fun2(x):
	return x * x

def fun3(x):
	return (x[0] + 3.0) ** 2 + (x[1] + 5.0) ** 2 + (x[2] + 10.0) ** 2

def fun4(x):
	return cos(x[0] - x[1])

def fun5(x):
	return x[0]**3 + (x[0] - 1) * x[1]**2 - x[0]*x[1] + x[1]**2 + x[0]*x[1]**3

def fun6(x, inequality_restrictions):
	return  x[0] ** 2 - x[1] ** 2 - inequality_restrictions[0] * x[0]  - inequality_restrictions[1] * x[1] + inequality_restrictions[2] *(x[0] + x[1] - 4)

arr = [0, 0, 0]
arr[0] = input("Inserte constante p1: ")
arr[1] = input("Inserte constante p2: ")
arr[2] = input("Inserte constante p3: ")

print gradient_descent(fun6, np.array([2, 2]), 0.1, 0.1, arr)
# print hessian(fun4, np.array([0.0, 0.0]))