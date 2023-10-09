// Tyler Hostler-Mathis

#include <bits/stdc++.h>
using namespace std;

// Struct to store information about some given Porcupine
struct porcupine {
  int a, h, l;
  porcupine() {}
  porcupine(int a, int h, int l) : a(a), h(h), l(l) {}
};

void solve(int d) {
  // Take input
  int n, m;
  cin >> n >> m;

  // Keep track of the levels of fighters at certain healths
  map<int, map<int, int>> healthLevelFreq;
  // And a sorted set of healths
  set<int> healths;

  // Take all information about the fighters
  // This uses a cool little C++ trick, referential
  // structured bindings. When we say auto [a, h, l] : fighters,
  // we tell the compiler to actually get a reference to the first
  // three members defined in the porcupine struct. Then we can scan
  // directly into them.
  vector<porcupine> fighters(n + m);
  for (auto &[a, h, l] : fighters) {
    // Take data
    cin >> a >> h >> l;

    // Increase freq
    healthLevelFreq[h][l]++;

    // Insert the new health
    healths.insert(h);
  }

  // Keep iterator to current health death location
  set<int>::iterator healthLoc = healths.begin();

  // Keep the current exploded damage
  long long explodedDamage = 0;

  // And keep a pointer to the left and right fighters
  int left = n - 1, right = n;

  // While both teams have players
  vector<bool> processed(n + m, false);
  while (left >= 0 && right < n + m) {
    // Get our figher info
    auto &[la, lh, ll] = fighters[left];
    auto &[ra, rh, rl] = fighters[right];

    // Take them out of the table, if we have not done so already
    if (!processed[left])
      healthLevelFreq[lh][ll]--;
    if (!processed[right])
      healthLevelFreq[rh][rl]--;

    processed[left] = true;
    processed[right] = true;

    // Fight! Find out how many hits it will take until one dies
    int hitsToKillLeft = ceil(((double)lh - explodedDamage) / ra);
    int hitsToKillRight = ceil(((double)rh - explodedDamage) / la);
    int hitsUsed = min(hitsToKillLeft, hitsToKillRight);

    // Take the damage
    lh -= ra * hitsUsed;
    rh -= la * hitsUsed;

    // Check if left died
    long long newDamage = 0;
    bool leftDied = false;
    if (lh - explodedDamage <= 0) {
      // Register it's explosion
      newDamage += 2 * ll;

      // Move left
      left--;

      leftDied = true;
    }

    // Check if right died
    bool rightDied = false;
    if (rh - explodedDamage <= 0) {
      // Register it's explosion
      newDamage += 2 * rl;

      // Move right
      right++;

      rightDied = true;
    }

    // Now we can register the new explosion damage
    explodedDamage += newDamage;

    // Now that explodedDamage has potentially changed
    // we need to see if other people have died
    while (healthLoc != healths.end() && *healthLoc <= explodedDamage) {
      for (auto [level, cnt] : healthLevelFreq[*healthLoc])
        explodedDamage += 2LL * level * cnt;
      healthLoc++;

      // This explosion might have killed our unkilled left or right, check that
      if (!leftDied && lh - explodedDamage <= 0) {
        // Register it's explosion
        explodedDamage += 2 * ll;

        // Move left
        left--;
        
        leftDied = true;
      }

      if (!rightDied && lh - explodedDamage <= 0) {
        // Register it's explosion
        explodedDamage += 2 * rl;

        // Move right
        right++;
        
        rightDied = true;
      }
    }

    // Forget people who have already been killed by spike damage
    while (left >= 0 && fighters[left].h <= explodedDamage)
      left--;
    while (right < n + m && fighters[right].h <= explodedDamage)
      right++;
  }

  cout << "Day #" << d << ": ";
  // If both pointers moved all the way, draw
  if (left < 0 && right >= n + m)
    cout << "Draw!";
  // If only the right player is out of players, Jean wins
  else if (right >= n + m)
    cout << "Jean Wins!";
  // Otherwise, Pierre wins!
  else
    cout << "Pierre Wins!";
  cout << "\n";
}

int main() {
  cin.tie(0)->sync_with_stdio(0);
  cin.exceptions(cin.failbit);

  // Take the number of test cases, and process them
  int D;
  cin >> D;
  for (int d = 1; d <= D; d++)
    solve(d);

  return 0;
}
