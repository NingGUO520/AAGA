open td2
class  Node:
	def __init__(self):
		self.left = None
		self.right = None

	def setEnfant(self,l,r):
		self.left = l
		self.right = r

	def setLeft(self,l)
		self.left = l

	def setRight(self,r)
		self.right = r
# nomFile = str+".txt"
# fichier = open(nomFile,"w")
# fichier.close()


# exercice 2
def arbre2str(arbre):
	if arbre == None:
		return ""
	res = "("
	res += arbre2str(arbre.left)
	res += arbre2str(arbre.right)
	res+=")"
	return res 

# a = Node()
# a1 =  Node()
# a2 =  Node()
# a3 =  Node()
# a4 =  Node()

# a.setEnfant(a1,a2)
# a2.setEnfant(a3,a4)
# print(arbre2str(a))
print(genererEntier(10))

# Generateur de Remy
# def genererRemy(taille):
# 	tableau = {}
# 	i = 1
# 	racine = Node()
# 	tableau[i] = racine
# 	courant = racine
# 	while i<taille:
# 		i+=1
# 		interne = Node()
# 		feuile = Node()
# 		tab

# 		bit = bit_suivant()
# 		if bit == 0:
# 			interne.setLeft(courant)
# 			interne.setRight(feuile)

# 		else:





