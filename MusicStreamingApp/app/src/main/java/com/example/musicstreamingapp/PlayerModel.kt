package com.example.musicstreamingapp

data class PlayerModel(
   private val playMusicList: List<MusicModel> = emptyList(),
    var currentposition: Int = -1,
    var iswatchPlaylistView: Boolean = true
) {
    fun getAdapterModels(): List<MusicModel> {
        return playMusicList.mapIndexed { index, musicModel ->
            val newItem = musicModel.copy(
                isPlaying = index == currentposition
            )
            newItem
        }
    }

    fun updateCurrentPosition(musicModel: MusicModel){
        currentposition = playMusicList.indexOf(musicModel)
    }

    fun nextMusic() : MusicModel? {
        if (playMusicList.isEmpty()) return null
        currentposition = if((currentposition + 1) == playMusicList.size) 0 else currentposition +1
        return playMusicList[currentposition]
    }
    fun prevMusic() : MusicModel? {
        if (playMusicList.isEmpty()) return null
        currentposition = if((currentposition - 1) < 0) playMusicList.lastIndex else currentposition - 1
        return playMusicList[currentposition]
    }
    fun currentMusicModel() : MusicModel? {
        if(playMusicList.isEmpty()) return null
        return playMusicList[currentposition]
    }

}
