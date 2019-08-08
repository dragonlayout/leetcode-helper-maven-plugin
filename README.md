# leetcode-maven-plugin

This plugin will let you focus on the leetcode problem's solutions and generate Solution and Test scaffold code automatically for you.

# User Guide
1. Download the project, and run `mvn clean install`
2. Add leetcode-maven-plugin to your pom.xml plugins
    ```xml
    <build>
        <plugins>
            <plugin>
                <groupId>com.dragonlayout</groupId>
                <artifactId>leetcode-maven-plugin</artifactId>
                <version>1.4-SNAPSHOT</version>
            </plugin>
        </plugins>
    </build>
    ```
3. Create problem.properties file in your resources folder, for example:
    ``` properties
    package.name=com.dragonlayout.leetcode.solutions
    problem.category=easy
    problem.no=0167
    problem.name=Two Sum II
    problem.description=\
    Given an array of integers that is already sorted in ascending order, find two numbers such that they add up to a specific target number.\n\
    The function twoSum should return indices of the two numbers such that they add up to the target, where index1 must be less than index2.\n\
    \n\
    Note:\n\
    Your returned answers (both index1 and index2) are not zero-based.\n\
    You may assume that each input would have exactly one solution and you may not use the same element twice.\n\
    Example:\n\
    \n\
    Input: numbers = [2,7,11,15], target = 9\n\
    Output: [1,2]\n\
    Explanation: The sum of 2 and 7 is 9. Therefore index1 = 1, index2 = 2.
    problem.method.structure=public int[] twoSum(int[] numbers, int target) {}
    ```
    you should add `\n\` at the end of each line of the problem.description.
4. Execute `mvn leetcode:generateSolution` in your leetcode-solution project will generate one Solution.java and SolutionTest.java. If you want to add another solution, such as Solution1.java. Just execute the `mvn leetcode:generateSolution` again will generate Solution1.java and add Solution1 field member to SolutionTest.java.
    
    Now you can see the Solution and Test file in your project like this,
    ```
        -src
            -main
                -java
                    -package.name
                        -problem.category
                            -_problem.no_problem.name
                                -Solution.java
                                -Solution1.java
            -test
                -java
                    -package.name
                        -problem.category
                            -_problem.no_problem.name
                                -SolutionTest.java
    ```
    ```java
    package com.dragonlayout.leetcode.solutions.easy._0167_two_sum_ii;
    
    // Date: 2019/08/08 09:46:01
    
    // Given an array of integers that is already sorted in ascending order, find two numbers such that they add up to a specific target number.
    // The function twoSum should return indices of the two numbers such that they add up to the target, where index1 must be less than index2.
    
    // Note:
    // Your returned answers (both index1 and index2) are not zero-based.
    // You may assume that each input would have exactly one solution and you may not use the same element twice.
    // Example:
    
    // Input: numbers = [2,7,11,15], target = 9
    // Output: [1,2]
    // Explanation: The sum of 2 and 7 is 9. Therefore index1 = 1, index2 = 2.
    
    
    // Time complexity:
    // Space complexity:
    
    // Notes:
    
    public class Solution {
    
        public int[] twoSum(int[] numbers, int target) {}
    }
    ```
    ```java
    package com.dragonlayout.leetcode.solutions.easy._0167_two_sum_ii;
    
    import org.junit.Before;
    import org.junit.Test;
    import org.junit.runner.RunWith;
    import org.junit.runners.Parameterized;
    import org.junit.runners.Parameterized.Parameters;
    
    import java.util.Arrays;
    import java.util.Collection;
    
    import static org.hamcrest.CoreMatchers.equalTo;
    import static org.hamcrest.CoreMatchers.is;
    import static org.junit.Assert.assertThat;
    
    @RunWith(Parameterized.class)
    public class SolutionTest {
    
        private int[] numbers;
        private int target;
    
        private int[] expected;
    
        private Solution solution;
        private Solution1 solution1;
    
        public SolutionTest(int[] numbers, int target, int[] expected) {
            this.numbers = numbers;
            this.target = target;
            this.expected = expected;
        }
    
        @Parameters
        public static Collection<Object[]> testCases() {
            return Arrays.asList(new Object[][]{
                // todo add your test cases here
            });
        }
    
        @Before
        public void setUp() throws Exception {
            solution = new Solution();
            solution1 = new Solution1();
        }
    
        @Test
        public void twoSum() {
            int[] solutionActual = solution.twoSum(numbers, target);
            assertThat(solutionActual, is(equalTo(expected)));
    
            int[] solution1Actual = solution1.twoSum(numbers, target);
            assertThat(solution1Actual, is(equalTo(expected)));
    
        }
    }
    ```
5. Add your test cases to the SolutionTest.testCases() and everything is done.