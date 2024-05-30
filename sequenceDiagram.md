```mermaid

sequenceDiagram
    Actor User
    participant App
    participant GameThread
    participant GameArea
    participant Leaderboard
    participant datFile

    %% ゲーム開始
    datFile ->>Leaderboard :loadLeaderboard()
    Leaderboard ->>App :this.leaderboard = loadLeaderboard()
    User->>App: main() メソッドを呼び出す
    App->>GameThread: start()

    %% ミノの操作
    User->>App: キー入力
    App->>App: repaint()


    %% 一時停止
    User->>App: キー入力 (P)
    App->>App: togglePause()

    %% ゲームスレッドの処理
    loop ゲームループ・画面更新
        alt ゲームが一時停止中
            GameThread->>GameThread: Thread.sleep(100)
        else ゲームが進行中
            GameThread->>GameArea: isCollison(mino, mino.getMinoX(), mino.getMinoY() + 1, mino.getMinoAngle())
            alt 衝突なし
                GameThread->>GameArea: moveDown(mino)
            else 衝突あり
                GameThread->>GameArea: isCollison(mino)
                alt ゲームオーバーの場合
                    GameThread->>App: gameOver()
                    Activate datFile
                    App->>Leaderboard: saveLeaderboard()でadd score　
                    Leaderboard->>datFile :saveLeaderboard()　save to file
                    
                    datFile->>App: displayLeaderboard()
                    deactivate datFile
                    App->>System: System.exit(0)
                else ゲーム続行の場合→ミノの固定
                    GameThread->>GameArea: bufferFieldAddMino(mino)
                    GameThread->>GameArea: eraseLine()
                    GameThread->>GameArea: initField()
                    GameThread->>App: updateMino(nextMino)
                    App->>App: updateNextMino(new Mino())
                end
            end
        end
        GameThread->>App: repaint()
        GameThread->>GameArea: drawNextMino(nextMino)
        GameThread->>GameThread: increaseDifficulty()
        GameThread->>GameThread: Thread.sleep(sleepTime)
    end
```
