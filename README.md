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
                <version>1.5-SNAPSHOT</version>
            </plugin>
        </plugins>
    </build>
    ```
3. Create problem.properties file in your resources folder, for example:
    ``` properties
    package.name=com.dragonlayout.leetcode.solutions
    problem.category=easy
    problem.no=0001
    problem.name=Two Sum
    problem.description=\
    Given an array of integers, return indices of the two numbers such that they add up to a specific target.\n\
    You may assume that each input would have exactly one solution, and you may not use the same element twice.\n\
    \n\
    Example:\n\
    \n\
    Given nums = [2, 7, 11, 15], target = 9,\n\
    \n\
    Because nums[0] + nums[1] = 2 + 7 = 9,\n\
    return [0, 1].
    problem.method.structure=public int[] twoSum(int[] nums, int target) {}
    ```
    you should add `\n\` at the end of each line of the problem.description.
4. Execute `mvn leetcode:generateSolution` in your leetcode-solution project will generate one Solution.java(interface), Solution1.java and SolutionTest.java. If you want to add another solution, such as Solution2.java. Just execute the `mvn leetcode:generateSolution` again will generate Solution2.java and add Solution2 field member to SolutionTest.java.
    
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
    
    public interface Solution {
    
        public int[] twoSum(int[] numbers, int target);
    }
    ```
    ```java
    package com.dragonlayout.leetcode.solutions.easy._0001_two_sum;
    
    // Date: 2019/09/08 15:43:14
    
    // Given an array of integers, return indices of the two numbers such that they add up to a specific target.
    // You may assume that each input would have exactly one solution, and you may not use the same element twice.
    
    // Example:
    
    // Given nums = [2, 7, 11, 15], target = 9,
    
    // Because nums[0] + nums[1] = 2 + 7 = 9,
    // return [0, 1].
    
    
    // Time complexity:
    // Space complexity:
    
    // Notes:
    
    public class Solution1 implements Solution {
    
        @Override
        public int[] twoSum(int[] nums, int target) {}
    }
    ```
    ```java
    package com.dragonlayout.leetcode.solutions.easy._0001_two_sum;
    
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
    
        private int[] nums;
        private int target;
    
        private int[] expected;
    
        private Solution solution;
    
        public SolutionTest(int[] nums, int target, int[] expected) {
            this.nums = nums;
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
            solution = new Solution1();
        }
    
        @Test
        public void twoSum() {
            int[] actual = solution.twoSum(nums, target);
            assertThat(actual, is(equalTo(expected)));
        }
    }
    ```
5. Add your test cases to the SolutionTest.testCases() and everything is done.