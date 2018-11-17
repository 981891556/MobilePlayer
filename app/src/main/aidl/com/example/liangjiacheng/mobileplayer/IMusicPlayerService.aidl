// IMusicPlayerService.aidl
package com.example.liangjiacheng.mobileplayer;

// Declare any non-default types here with import statements

interface IMusicPlayerService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);


              /**
                 * 根据位置打开对应的音频
                 */
                 void openAudio(int position);

                /**
                 *播放音乐
                 */
                 void start();

                /**
                 * 暂停播放
                 */
                  void pause();

                /**
                 * 停止播放
                 */
                 void stop();


                /**得到当前播放进度
                 * @return
                 */
                 int getCurrentPosition();

                /**得到当前音频的总时长
                 * @return
                 */
                 int getDuration();



                /**得到播放歌曲的艺术家（演唱者）
                 * @return
                 */
                 String getArtist();

                 String getName();

                /**得到播放歌曲的路径
                 * @return
                 */
                 String getAudioPath();

                /**
                 * 播放下一个视频
                 */
                 void next();

                /**
                 * 播放上一个视频
                 */
                 void pre();

                /**设置播放模式
                 * @param playmode
                 */
                 void setPlayMode(int playmode);

                /**得到播放模式
                 * @return
                 */
                  int getPlayMode();

//                  是否正在播放
                   boolean isPlaying();

//                  拖动音频
                 void seekTo(int position);
}
