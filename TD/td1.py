def rand48(graine):
	mult = 25214903917
	incr = 11
	m = 2**48-1
	v = (graine*mult+ incr)&m
	
	v = v>>16
	if (v & 2**31):
		v-=2**32
	return v
graine = 156079716630527
print(rand48(graine))