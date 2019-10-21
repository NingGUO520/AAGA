def lcg48(graine,nb):
	v1 = graine
	mult = 25214903917
	incr = 11
	mask = 2**48 -1 
	for i in range(nb):
		v1 = (v1*mult+ incr)&mask
		v2 = v1>>16

		if v2&(2**31):
			v2 -= 2**32

		print(v2)
	return None


graine = 156079716630527
lcg48(graine,5)