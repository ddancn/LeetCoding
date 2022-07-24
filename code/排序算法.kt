import java.util.*

/**
 * @author ddancn
 * @date 2020/7/2
 *
 */

 //-------------------下面是测试程序-------------------

fun main() {
//    sort(::bubbleSort, "冒泡")
//    sort(::insertSort, "插入")
//    sort(::selectSort, "选择")
//    sort(::shellSort, "希尔")
//    sort(::mergeSort, "归并")
    sort(::quickSort, "快速", 5)
//    sort(::quickSort2, "双轴快速")
//    perform(times = 5000, length = 1000)
}

// 生成一个随机数组，并用给定的排序方法排序，输出结果
fun sort(sort: (IntArray) -> Unit, tag: String, length: Int = 10) {
    val a = generateArray(length)
    println("原始数组：${a.me()}")
    sort(a)
    println("${tag}排序数组：${a.me()}")
}

// 比较多个排序方法应用在相同数组上的耗时
fun perform(times: Int = 1000, length: Int = 1000) {
    val arrays = Array(times) { generateArray(length) }
    println("排序 $times 个长度为 $length 的数组...")
    perform(arrays.deepCopyOf(), ::bubbleSort, "冒泡")
    perform(arrays.deepCopyOf(), ::insertSort, "插入")
    perform(arrays.deepCopyOf(), ::selectSort, "选择")
    perform(arrays.deepCopyOf(), ::shellSort, "希尔")
    perform(arrays.deepCopyOf(), ::mergeSort, "归并")
    perform(arrays.deepCopyOf(), ::quickSort, "快速")
    perform(arrays.deepCopyOf(), ::quickSort2, "双轴快速")
}

// 测试给定排序方法应用在给定数组上的耗时
fun perform(arrays: Array<IntArray>, sort: (IntArray) -> Unit, tag: String) {
    val startTime = System.currentTimeMillis()
    arrays.forEach(sort)
    val endTime = System.currentTimeMillis()
    println("${tag}排序耗时 ${endTime - startTime} ms")
}

//-------------------下面是排序算法-------------------

/**
 * 冒泡：从左到右不断交换相邻逆序的相邻元素，在一轮的交换之后，可以让已排序的元素移动到右侧
 * flag标志位：如果一轮排序没有交换的话，可认为数组已有序，提前退出
 */
fun bubbleSort(a: IntArray) {
    val n = a.size
    // 外层循环表示需要进行的趟数
    for (i in 0 until n) {
        var flag = false
        // j的取值范围：-i是已有序的部分，-1是只需要循环到倒数第二个（它会跟倒一比较）
        for (j in 0 until n - 1 - i) {
            // 比较相邻的两个数，是逆序的则交换它们
            if (a[j] > a[j + 1]) {
                swap(a, j, j + 1)
                flag = true
            }
        }
        if (!flag) break
    }
}

/**
 * 插入：令左侧数组有序，在右侧未排序数组中选择第一个数(value)，插入到左侧数组中合适的位置
 * value 从尾到头地依次和左侧数组比较大小，如果 value 较小，说明还需继续往前找，此时被比较的数(a[j])后移一位，给 value 腾位置
 *
 * 另一种方式是 value 与 a[j] 交换位置，达到 value 前移的效果。但交换两个数需要3次赋值，移动 a[j] 只需要1次，如果交换实测会大大降低效率
 */
fun insertSort(a: IntArray) {
    val n = a.size
    // 外层循环确定有序数组的边界，i之前都是有序的
    for (i in 1 until n) {
        // 先保存下要插入的值，因为这个位置等下可能会被覆盖
        val value = a[i]
        // 从尾到头比较
        var j = i - 1
        while (j >= 0) {
            // 后移一位，继续往前比较
            if (a[j] > value) a[j + 1] = a[j]
            // 找到了插入位置
            else break
            j--
        }
        // 最后一次循环找到了第一个比value小的值，所以+1放在它后面
        a[j + 1] = value
    }
}

/**
 * 选择：令左侧数组有序，从右侧未排序数组中选择一个最小值，与左侧数组的最右边的数交换
 */
fun selectSort(a: IntArray) {
    val n = a.size
    // 外层循环确定有序数组的边界，i之前都是有序的
    for (i in 0 until n - 1) {
        // 找到未排序数组中最小值的下标
        var minIndex = i
        for (j in i + 1 until n)
            if (a[j] < a[minIndex])
                minIndex = j
        // 放到有序数组的后面
        swap(a, i, minIndex)
    }
}

/**
 * 希尔：使用插入排序对间隔 h 的序列进行排序。不断减小 h，最后令 h=1，就可以使得整个数组是有序的
 *
 * 插入排序一次只能减少 1 个逆序对，希尔排序由于间隔着交换，可能减少多于 1 个，在针对大规模数组时能提高效率
 * 插入排序在数组较有序时效果好，按照希尔排序的思路，数组会越来越有序，可以给每次的插入排序提供便捷
 */
fun shellSort(a: IntArray) {
    val n = a.size
    var h = 1
    // 根据数组大小选择步长h，步长最好是互质的
    // 这里用了1, 4, 13, 40...这个数列
    while (h < n / 3) h = 3 * h + 1

    // h==1时就是普通的插入排序，执行后数组就有序了
    while (h >= 1) {
        // 这里和插入排序基本一致，由比较相邻的数变为比较间隔为h的数
        for (i in h until n) {
            val value = a[i]
            var j = i - h
            while (j >= 0) {
                if (a[j] > value) a[j + h] = a[j]
                else break
                j -= h
            }
            a[j + h] = value
        }
        // 减小步长
        h /= 3
    }
}

/**
 * 归并：将数组平分成两个部分，通过递归使其分别有序（递归到长度等于1自然有序），再将前后两部分有序数组合并起来
 * 合并过程需要用到一个临时数组。将其作为参数传递重复使用，可以减少递归每层申请和释放数组的开销
 * 有办法不用临时数组吗？会很麻烦，无法在On时间内实现
 */
fun mergeSort(a: IntArray) {
    mergeSort(a, 0, a.size - 1, IntArray(a.size))
}

fun mergeSort(a: IntArray, l: Int, r: Int, ta: IntArray) {
    if (l >= r) return
    val m = l + (r - l) / 2
    mergeSort(a, l, m, ta)
    mergeSort(a, m + 1, r, ta)
    merge(a, l, m, r, ta)
}

fun merge(a: IntArray, l: Int, m: Int, r: Int, ta: IntArray) {
    var i = l
    var j = m + 1
    var k = 0
    // 将数组前半部分和后半部分的数依次拿来比较，较小者放到临时数组中
    while (i <= m && j <= r)
        ta[k++] = if (a[i] <= a[j]) a[i++] else a[j++]

    // 将比较后剩下的元素复制到临时数组
    while (i <= m) ta[k++] = a[i++]
    while (j <= r) ta[k++] = a[j++]
    // 再将临时数组的有序部分复制到原数组
    System.arraycopy(ta, 0, a, l, r - l + 1)
}

/**
 * 快排：选定一个元素，将数组中大于它的放到它的右边，小于它的放到它的左边，再对左右两边进行同样的递归
 */
fun quickSort(a: IntArray) {
    quickSort(a, 0, a.size - 1)
}

fun quickSort(a: IntArray, l: Int, r: Int) {
    if (l >= r) return

    val m = partition(a, l, r)
    quickSort(a, l, m - 1)
    quickSort(a, m + 1, r)
}

fun partition(a: IntArray, l: Int, r: Int): Int {
    // 选择最后一个数作为哨兵
    val pivot = a[r]
    // i意图指向第一个大于pivot的位置
    var i = l
    for (j in l until r) {
        // 将小于pivot的值换到前面，大于pivot的跳过
        if (a[j] < pivot) {
            swap(a, i, j)
            i++
        }
    }
    // 将pivot换到中间
    swap(a, i, r)
    return i
}

/**
 * 双轴快排：递归思想不变。使用双指针，在一趟快排中减少交换次数，提高效率
 * 缺点是难以理解或人脑调试，特别是在特殊情况、循环退出等场景
 */
fun quickSort2(a: IntArray) {
    quickSort2(a, 0, a.size - 1)
}

fun quickSort2(a: IntArray, left: Int, right: Int) {
    if (left >= right) return

    // 选择第一个数作为哨兵
    val pivot = a[left]
    // 左右指针向中间逼近
    var i = left
    var j = right

    while (i < j) {
        // 右指针跳过所有大于pivot的数
        while (a[j] >= pivot && i < j) j--
        // 此时a[j]<pivot，填到i的坑里
        a[i] = a[j]
        // 左指针跳过所有小于pivot的数
        while (a[i] <= pivot && i < j) i++
        // 此时a[i]>pivot，填到j的坑里
        a[j] = a[i]
    }
    // 循环结束时i还是坑着的，把pivot填进去
    a[i] = pivot

    quickSort2(a, left, i - 1)
    quickSort2(a, i + 1, right)
}

//-------------------下面是辅助函数-------------------

// 生成随机数组，用length指定长度
fun generateArray(length: Int = (0..50).random()): IntArray = with(Random()) {
    IntArray(length) { nextInt(100) }
}

// 交换数组a中下标为i和j的值
fun swap(a: IntArray, i: Int, j: Int) {
    a[i] = a[j].also { a[j] = a[i] }
}

// toString
fun IntArray.me() = "[${joinToString(separator = ", ")}]"

// 二维数据的深拷贝，为了让不同的排序方法使用上同样的数组
fun Array<IntArray>.deepCopyOf(): Array<IntArray> = Array(size) { n -> IntArray(this[n].size) { i -> this[n][i] } }
