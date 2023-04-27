package com.example.stcompose.brick

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Integer.min

/**
 *    author : heyueyang
 *    time   : 2023/04/17
 *    desc   : 业务逻辑的处理
 *    version: 1.0
 */
class GameViewModel : ViewModel() {
    private val _viewState: MutableState<ViewState> = mutableStateOf(ViewState())
    val viewState: State<ViewState> = _viewState

    fun dispatch(action: Action) {
        val state = viewState.value
        viewModelScope.launch {
            when (action) {
                Action.Reset -> run {
                    //重置游戏
                    if (state.gameStatus == GameStatus.Onboard || state.gameStatus == GameStatus.GameOver) {
                        return@run emit(
                            ViewState(
                                gameStatus = GameStatus.Running,
                                isMute = state.isMute
                            )
                        )
                    }
                    emit(state.copy(
                        gameStatus = GameStatus.ScreenClearing
                    ).also {
                        launch {
                            cleanScreen(state)
                            emit(
                                ViewState(
                                    gameStatus = GameStatus.Onboard,
                                    isMute = state.isMute
                                )
                            )
                        }
                    })
                }

                Action.GameTick -> run {
                    //开始后的自动下落，更新下一个方块的数据等等。。。
                    //游戏还没开始
                    if (!state.isRunning) {
                        return@run emit(state)
                    }
                    //开始了自动下落
                    if (state.spirit != Spirit.Empty) {
                        //如何判断到底并获取下一个方块的呢，就是这里如果还下移，那么超出了范围，但是游戏没有结束，那么就取下一个
                        val newSpirit = state.spirit.moveBy(Direction.Down.toOffset())
                        //方块还在有效范围内，就是还没到顶
                        if (newSpirit.isValidInMatrix(state.bricks, state.matrix)) {
                            return@run emit(state.copy(spirit = newSpirit))
                        }
                    }
                    //判断是否游戏结束
                    if (!state.spirit.isValidInMatrix(state.bricks, state.matrix, true)) {
                        return@run emit(
                            state.copy(gameStatus = GameStatus.ScreenClearing).also {
                                launch {
                                    emit(
                                        cleanScreen(state = state).copy(gameStatus = GameStatus.GameOver)
                                    )
                                }
                            }
                        )
                    }
                    //更新下一个方块
                    val (updateBricks, cleanLine) = updateBricks(
                        state.bricks,
                        state.spirit,
                        state.matrix
                    )
                    val (noClean, cleaning, cleaned) = updateBricks
                    val newState = state.copy(
                        spirit = state.spiritNext,
                        spiritReserve = (state.spiritReserve - state.spiritNext).takeIf { it.isNotEmpty() }
                            ?: generateSpiritReverse(state.matrix),
                        line = state.line + cleanLine,
                        score =
                        //分数计算规则：消除行（1-4）的分数+每个下落模块的分数（固定12分）
                        state.score + calculateScore(cleanLine) +
                                if (state.spirit != Spirit.Empty) ScoreEverySpirit else 0
                    )

                    if (cleanLine != 0) {
                        //让需要消除的闪5下
                        state.copy(gameStatus = GameStatus.LineClearing).apply {
                            repeat(5) {
                                emit(
                                    state.copy(
                                        gameStatus = GameStatus.LineClearing,
                                        bricks = if (it % 2 == 0) noClean else cleaning,
                                        spirit = Spirit.Empty
                                    )
                                )
                                delay(100)
                            }
                        }
                        emit(newState.copy(bricks = cleaned, gameStatus = GameStatus.Running))
                    } else {
                        emit(newState.copy(bricks = noClean))
                    }
                }

                Action.Pause -> run {
                    //暂停
                    Log.e("TAG", "Pause:${state.toString()}")
                    if (state.gameStatus == GameStatus.Running) {
                        emit(state.copy(gameStatus = GameStatus.Paused))
                    } else {
                        emit(state)
                    }
                }

                Action.Drop -> run {
                    //下落
                    if (!state.isRunning) {
                        return@run emit(state = state)
                    }
                    var i = 0
                    //获取能够下落的最大值
                    while (state.spirit.moveBy(0 to ++i)
                            .isValidInMatrix(state.bricks, state.matrix)
                    ) {
                    }
                    val newSpirit = state.spirit.moveBy(0 to i - 1)
                    emit(state.copy(spirit = newSpirit))
                }

                Action.Mute -> run {
                    //静音
                    emit(state.copy(isMute = !state.isMute))
                }

                Action.Resume -> run {
                    //重新开始
                    Log.e("TAG", "Resume:${state.toString()}")
                    if (state.isPaused) {
                        emit(state.copy(gameStatus = GameStatus.Running))
                    } else {
                        emit(state)
                    }
                }

                Action.Rotate -> run {
                    //旋转
                    if (!state.isRunning) {
                        return@run emit(state)
                    }
                    val newSpirit = state.spirit.rotate().adjustOffset(state.matrix)
                    if (newSpirit.isValidInMatrix(state.bricks, state.matrix)) {
                        //新的变换是在有效范围内，如果超出了矩阵范围，那么就不响应这次的变化，将之前的状态返回即可
                        emit(state.copy(spirit = newSpirit))
                    } else {
                        emit(state)
                    }
                }

                is Action.Move -> run {
                    //移动
                    if (!state.isRunning) {
                        return@run emit(state)
                    }
                    val offset = action.direction.toOffset()
                    val newSpirit = state.spirit.moveBy(offset)
                    if (newSpirit.isValidInMatrix(state.bricks, state.matrix)) {
                        emit(state.copy(spirit = newSpirit))
                    } else {
                        emit(state)
                    }
                }
            }
        }
    }


    /**
     * 更新所有的方块状态
     *
     * @param curBricks 已经落到底部的方块
     * @param spirit 当前下落的方块
     * @param matrix 矩阵范围
     * @return Pair两个参数
     */
    private fun updateBricks(
        curBricks: List<Brick>,
        spirit: Spirit,
        matrix: Pair<Int, Int>
    ): Pair<Triple<List<Brick>, List<Brick>, List<Brick>>, Int> {
        //将屏幕中所有的的砖块转为用行存储的的map<行，set<行中砖块的x坐标>>
        val bricks = curBricks + Brick.of(spirit)
        val map = mutableMapOf<Float, MutableSet<Float>>()
        bricks.forEach {
            //y就是方块所在的行，如果行没有方块元素，方一个空的set
            map.getOrPut(it.location.y) {
                mutableSetOf()
            }.add(it.location.x)
        }
        //过滤出三个值，需要消除的行，以及不可消除的行，消除行后，比这个行大的向下移动一行
        var cleaning = bricks
        var cleaned = bricks
        val cleanLine = map.entries.sortedBy { it.key }.filter {
            //能够被消除的行必然是沾满所有横行空格的
            it.value.size == matrix.first
        }.map { it.key }
            //onEach是扩展函数，就是将这个list中的数据过一遍并返回自己，和拿cleanLine自己做ForEach一个结果
            .onEach { line ->
                //所有不需要消除的行
                cleaning = cleaning.filter { it.location.y != line }
                //所有不需要消除的行，并当前的行是在消除行上方的，那么下移一行
                cleaned = cleaned.filter { it.location.y != line }.map {
                    if (it.location.y < line) it.offsetBy(0 to 1) else it
                }
            }

        return Triple(bricks, cleaning, cleaned) to cleanLine.size
    }


    /**
     * 清楚屏幕的一个效果
     *
     * @param state
     * @return
     */
    private suspend fun cleanScreen(state: ViewState): ViewState {
        val xRange = 0 until state.matrix.first
        var newState = state
        //从下往上的
        (state.matrix.second downTo 0).forEach { y ->
            emit(
                state.copy(
                    gameStatus = GameStatus.ScreenClearing,
                    bricks = state.bricks + Brick.of(xRange, y until state.matrix.second)
                )
            )
            delay(50)
        }
        //从上往下
        (0..state.matrix.second).forEach { y ->
            emit(
                state.copy(
                    gameStatus = GameStatus.ScreenClearing,
                    bricks = Brick.of(xRange, y until state.matrix.second),
                    //从上到下的时候把当前的方块清除掉
                    spirit = Spirit.Empty
                ).also {
                    newState = it
                }
            )
            delay(50)
        }
        return newState
    }

    private fun emit(state: ViewState) {
        _viewState.value = state
    }


}

/**
 * 视图的状态
 *
 * @property bricks 已经在底部的方块
 * @property spirit 下落的方块
 * @property spiritReserve 后续要下落的方块
 * @property matrix 游戏方块的矩阵
 * @property gameStatus 游戏状态
 * @property score 分数
 * @property line 总的下落行数
 * @property isMute 是否静音
 */
data class ViewState(
    val bricks: List<Brick> = emptyList(),
    val spirit: Spirit = Spirit.Empty,
    val spiritReserve: List<Spirit> = emptyList(),
    val matrix: Pair<Int, Int> = MatrixWidth to MatrixHeight,
    val gameStatus: GameStatus = GameStatus.Onboard,
    val score: Int = 0,
    val line: Int = 0,
    val isMute: Boolean = false
) {
    /**
     * 游戏级别
     */
    val level: Int
        //最高15级别 --
        get() = min(15, 1 + line / 20)

    /**
     * 下一个要下落的方块，从下落列表里取第一个
     */
    val spiritNext: Spirit
        get() {
            Log.e("TAG", "${spiritReserve.firstOrNull()}")
            return spiritReserve.firstOrNull() ?: Spirit.Empty
        }

    val isPaused: Boolean
        get() = gameStatus == GameStatus.Paused

    val isRunning: Boolean
        get() = gameStatus == GameStatus.Running
}

/**
 * UI所能发送到Model的Action
 *
 */
sealed interface Action {
    data class Move(val direction: Direction) : Action
    object Reset : Action
    object Pause : Action
    object Resume : Action
    object Rotate : Action
    object Drop : Action
    object GameTick : Action
    object Mute : Action
}

/**
 * 游戏状态
 */
enum class GameStatus {
    Onboard, //游戏欢迎页
    Running, //游戏进行中
    LineClearing,// 消行动画中
    Paused,//游戏暂停
    ScreenClearing, //清屏动画中
    GameOver//游戏结束
}


private const val MatrixWidth = 12
private const val MatrixHeight = 24