# porcupine.py by Juan Moscoso
import math
from collections import defaultdict

days = int(input())
for day in range(1, days + 1):
	n, m = map(int, input().split())
	fighters = []

	# we use a set for healths
	# because we only care about unique healths
	healths = set()

	# we use healthLevelFreq to store the sum of levels
	# at each health
	# defaultdict allows a default value for mapping
	# in this case, it is 0
	healthLevelFreq = defaultdict(int)

	for i in range(n + m):
		attack, health, level = map(int, input().split())
		healthLevelFreq[health] += level
		healths.add(health)
		fighters.append([attack, health, level])

	# we use this to keep the sorted list of healths
	healths = sorted(list(healths))
	
	
	healthIdx = 0
	explosionDamage = 0

	# simulate the battle
	seen = [False] * (n + m)
	l, r = n - 1, n
	while l >= 0 and r < n + m:

		lAttack, lHealth, lLevel = fighters[l]
		rAttack, rHealth, rLevel = fighters[r]

		# have we used these guys for battle before?
		# if not subtract their level
		if not seen[l]:
			healthLevelFreq[lHealth] -= lLevel
		if not seen[r]:
			healthLevelFreq[rHealth] -= rLevel

		seen[l] = True
		seen[r] = True

		# number of hits it takes to kill porcupine at index l
		lhits = math.ceil((lHealth - explosionDamage) / rAttack)
		# number of hits it takes to kill porcupine at index r
		rhits = math.ceil((rHealth - explosionDamage) / lAttack)

		# number of hits it takes to end the battle between the two 
		# is the minimum
		hits = min(lhits, rhits)

		lHealth -= hits * rAttack 
		rHealth -= hits * lAttack

		# update the values in the fighters list
		fighters[l][1] = lHealth
		fighters[r][1] = rHealth

		# check which of our fighters died and 
		# add their level to the explosionDamage
		# we keep these bools to make sure we dont 
		# add twice to explosionDamage
		explodedLeft, explodedRight = False, False
		if lHealth - explosionDamage <= 0:
			explodedLeft = True
			explosionDamage += 2 * lLevel
			l -= 1

		if rHealth - explosionDamage <= 0:
			explodedRight = True
			explosionDamage += 2 * rLevel
			r += 1
		
		# loop through our healths and add 2 * levels if the porcupines at this health are dead are dead
		while healthIdx < len(healths) and healths[healthIdx] <= explosionDamage:
			explosionDamage += 2 * healthLevelFreq[healths[healthIdx]]
			healthIdx += 1

			# check if our explosions caused our original fighters to explode
			if not explodedLeft and lHealth - explosionDamage <= 0:
				explodedLeft = True
				explosionDamage += 2 * lLevel
				l -= 1

			if not explodedRight and rHealth - explosionDamage <= 0:
				explodedRight = True
				explosionDamage += 2 * rLevel
				r += 1

		# if our fighter is dead, make sure not to use him
		while l >= 0 and fighters[l][1] - explosionDamage <= 0:
			l -= 1
		while r < n + m and fighters[r][1] - explosionDamage <= 0:
			r += 1

	# if pointers are both out of range
	# it means it is a draw
	if l < 0 and r >= n + m:
		print(f"Day #{day}: Draw!")
	else:
		print(f"Day #{day}: Jean Wins!" if r >= n + m else f"Day #{day}: Pierre Wins!")
	
