import math
def lcg48(graine):
	mult = 25214903917
	incr = 11
	m = 2**48 
	v = (graine*mult+ incr)%m
	if v<0:
		v = v +2**64
	return v


def init(n):
	if n < 2**40:
		return lcg48(n+2**40)
	else:
		return lcg48(n)
cpt = 0
n = 123456789+2^40
r = 0

def bit_suivant():
	global n,cpt,r
	if cpt == 0:
		cpt = 48
		n = lcg48(n)
		r = n 


	bit = r%2
	r = r //2
	cpt-=1
	return bit

def genererEntier(bound):
	res = 0 
	nbBits = math.floor(math.log(bound,2))+1
	# print("nombre bits = ", nbBits)
	for i in range(nbBits):
		bit = bit_suivant()
		res = 2*res + bit
		# print(res)
	if res > bound:
		genererEntier(bound)
	else:
		return res 
# graine = 156079716630527
# lcg48(graine)
# print(lcg48(0))
# for i in range(10):
	# print(bit_suivant())
print(genererEntier(10))



