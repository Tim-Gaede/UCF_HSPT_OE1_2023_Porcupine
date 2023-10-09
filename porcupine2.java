import java.util.*;

public class porcupine2 {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int cases = scan.nextInt();
        for(int tt = 1; tt<=cases ;tt++){
            System.out.print("Day #"+tt+": ");
            int n = scan.nextInt(), m = scan.nextInt();
            int[] attack1 = new int[n];
            int[] hp1 = new int[n];
            int[] level1 = new int[n];

            int[] attack2 = new int[m];
            int[] hp2 = new int[m];
            int[] level2 = new int[m];

            porc[] one = new porc[n];
            porc[] two = new porc[m];

            for (int i = 0; i < n; i++) {
                attack1[i] = scan.nextInt();
                hp1[i] = scan.nextInt();
                level1[i] = scan.nextInt();
                one[i] = new porc(attack1[i], hp1[i], level1[i],i);
            }

            for (int i = 0; i < m; i++) {
                attack2[i] = scan.nextInt();
                hp2[i] = scan.nextInt();
                level2[i] = scan.nextInt();
                two[i] = new porc(attack2[i], hp2[i], level2[i],i);
            }

            Arrays.sort(one);
            Arrays.sort(two);
            long threshold = 0;
            int hpIdx1 = 0;
            int hpIdx2 = 0;

            int mid1 = n - 1;
            int mid2 = 0;

            int turn = 0;
            // 0 is draw, 1 is left wins, 2 is right wins.
            int ans = 0;
            boolean[] dead1= new boolean[n];
            boolean[] dead2= new boolean[m];

            while (mid1 >= 0 && mid2 < m) {
                int leftStrikeRes = 0;
                int rightStrikeRes = 0;
                leftStrikeRes = hpLeft(attack1[mid1], hp1[mid1],
                        attack2[mid2], hp2[mid2], threshold);
                rightStrikeRes = hpLeft(attack2[mid2], hp2[mid2],
                        attack1[mid1], hp1[mid1], threshold);
                hp1[mid1] = leftStrikeRes;
                hp2[mid2] = rightStrikeRes;

                if(leftStrikeRes==0){
                    dead1[mid1] = true;
                    threshold += 2*level1[mid1];
                }
                if(rightStrikeRes == 0){
                    dead2[mid2] = true;
                    threshold += 2*level2[mid2];
                }

                if(hp1[mid1]<=threshold && !dead1[mid1]){
                    dead1[mid1] = true;
                    threshold+=2*level1[mid1];
                }
                if(hp2[mid2]<=threshold && !dead2[mid2]){
                    dead2[mid2] = true;
                    threshold+=2*level2[mid2];
                }

                while ((hpIdx1 < n && one[hpIdx1].hp <= threshold)
                        || (hpIdx2 < m && two[hpIdx2].hp <= threshold)) {
                    if (hpIdx1 < n && one[hpIdx1].hp <= threshold) {
                        if (!dead1[one[hpIdx1].id])
                            threshold += 2 * one[hpIdx1].level;
                        hpIdx1++;
                    }
                    if (hpIdx2 < m && two[hpIdx2].hp <= threshold) {
                        if (!dead2[two[hpIdx2].id])
                            threshold += 2 * two[hpIdx2].level;
                        hpIdx2++;
                    }
                }

                while (mid1 >= 0 && hp1[mid1] <= threshold) {
                    mid1--;
                }
                while (mid2 < m && hp2[mid2] <= threshold) {
                    mid2++;
                }

                if (mid1 == -1 && mid2 == m) {
                    break;
                }
                if (mid2 == m) {
                    ans = 1;
                    break;
                }
                if (mid1 == -1) {
                    ans = 2;
                    break;
                }

                turn ^= 1;
            }
            if (ans == 0) {
                System.out.println("Draw!");
            } else if (ans == 1) {
                System.out.println("Jean Wins!");
            } else {
                System.out.println("Pierre Wins!");
            }
        }
    }

    static class porc implements Comparable<porc> {
        int at, hp, level, id;

        public porc(int at, int hp, int level, int id) {
            this.at = at;
            this.hp = hp;
            this.level = level;
            this.id = id;
        }

        @Override
        public int compareTo(porc o) {
            return this.hp - o.hp;
        }
    }

    static int hpLeft(int a1, int h1, int a2, int h2, long thresh) {
        //a1 goes first, make a1 be whoever its supposed to be depending on the turn.
        int ori = h1;
        h1 -= thresh;
        h2 -= thresh;
        if(h2 <= 0)return ori;
        int turnsTillDead1 = h1 / a2;
        if (h1 % a2 != 0) turnsTillDead1++;
        int turnsTillDead2 = h2 / a1;
        if (h2 % a1 != 0) turnsTillDead2++;
        if (turnsTillDead1 <= turnsTillDead2) {
            return 0;
        }
        return Math.max(0,ori - ((turnsTillDead2) * a2));
    }
}
