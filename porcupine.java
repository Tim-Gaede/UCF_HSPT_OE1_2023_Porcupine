import java.util.*;

//will add comments later

public class porcupine{
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int numDays = in.nextInt();
        for(int day = 1; day <= numDays; day++){
            System.out.print("Day #" + day + ": ");
            int n = in.nextInt(), m = in.nextInt();
            fighter fighters[] = new fighter[n+m];
            fighter left[] = new fighter[n], right[] = new fighter[m];
            for(int i = 0; i < n; i++){
                int a = in.nextInt(), h = in.nextInt(), l = in.nextInt();
                fighters[i] = new fighter(a, h, l);
                left[i] = new fighter(a, h, l);
            }
            for(int i = 0; i < m; i++){
                int a = in.nextInt(), h = in.nextInt(), l = in.nextInt();
                fighters[i+n] = new fighter(a, h, l);
                right[i] = new fighter(a, h, l);
            }
            solve(fighters, left, right, n, m);
        }
        in.close();
    }

    public static void solve(fighter fighters[], fighter left[], fighter right[], int n, int m){
        Map<Integer, Map<Integer, Integer>> freq = new HashMap<Integer, Map<Integer,Integer>>();
        for(int i = 0; i < n+m; i++){
            int health = fighters[i].health, level = fighters[i].level;
            Map<Integer, Integer> levelFreq = freq.get(health);
            if(levelFreq == null)
                levelFreq = new HashMap<Integer, Integer>();
            if(levelFreq.containsKey(level))
                levelFreq.put(level, levelFreq.get(level) + 1);
            else
                levelFreq.put(level, 1);
            freq.put(health, levelFreq);
        }

        Arrays.sort(fighters);
        int lIdx = n-1, rIdx = 0;
        int healthIdx = 0;
        long explosionDamage = 0;

        boolean seen[] = new boolean[n+m];
        Arrays.fill(seen, false);
        while(lIdx >= 0 && rIdx < m){
            int lAttack = left[lIdx].attack, rAttack = right[rIdx].attack,
                    lHealth = left[lIdx].health, rHealth = right[rIdx].health,
                    lLevel = left[lIdx].level, rLevel = right[rIdx].level;

            if(!seen[lIdx]){
                Map<Integer, Integer> levelFreq = freq.get(lHealth);
                levelFreq.put(lLevel, levelFreq.get(lLevel)-1);
                freq.put(lHealth, levelFreq);
            }
            if(!seen[rIdx + n]){
                Map<Integer, Integer> levelFreq = freq.get(rHealth);
                levelFreq.put(rLevel, levelFreq.get(rLevel)-1);
                freq.put(rHealth, levelFreq);
            }
            seen[lIdx] = seen[rIdx + n] = true;

            int numAttacksOnLeft = (int)Math.ceil((lHealth - explosionDamage) / (double)rAttack);
            int numAttacksOnRight = (int)Math.ceil((rHealth - explosionDamage) / (double)lAttack);
            int numAttacks = Math.min(numAttacksOnLeft, numAttacksOnRight);

            lHealth -= numAttacks * rAttack;
            rHealth -= numAttacks * lAttack;
            left[lIdx].health = lHealth;
            right[rIdx].health = rHealth;

            long currExplosion = 0;
            if(lHealth - explosionDamage <= 0){
                currExplosion += 2 * lLevel;
                lIdx--;
            }
            if(rHealth - explosionDamage <= 0){
                currExplosion += 2 * rLevel;
                rIdx++;
            }
            explosionDamage += currExplosion;

            while(healthIdx < n + m && fighters[healthIdx].health <= explosionDamage){
                if(healthIdx == 0 || fighters[healthIdx-1].health != fighters[healthIdx].health)
                    for(Map.Entry<Integer, Integer> item : freq.get(fighters[healthIdx].health).entrySet())
                        explosionDamage += 2L * item.getKey() * item.getValue();
                healthIdx++;
            }

            while(lIdx >= 0 && left[lIdx].health - explosionDamage <= 0)
                lIdx--;
            while(rIdx < m && right[rIdx].health - explosionDamage <= 0)
                rIdx++;
        }

        if(lIdx < 0 && rIdx >= m)
            System.out.println("Draw!");
        else if(lIdx < 0)
            System.out.println("Pierre Wins!");
        else
            System.out.println("Jean Wins!");
    }

    public static class fighter implements Comparable<fighter>{
        int attack, health, level, id;
        public fighter(int a, int h, int l){
            attack = a;
            health = h;
            level = l;
        }

        public int compareTo(fighter o) {
            return Integer.compare(health, o.health);
        }
    }
}