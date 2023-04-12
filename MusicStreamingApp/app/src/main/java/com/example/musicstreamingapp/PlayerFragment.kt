package com.example.musicstreamingapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.musicstreamingapp.databinding.FragmentPlayerBinding
import com.example.musicstreamingapp.service.MusicDto
import com.example.musicstreamingapp.service.MusicService
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class PlayerFragment : Fragment(R.layout.fragment_player) {

    private var model: PlayerModel = PlayerModel()
    private var binding: FragmentPlayerBinding? = null
    private var player: SimpleExoPlayer? = null
    private lateinit var playListAdapter: PlayListAdapter
    private val updateSeekRunnalbe = Runnable {
        UpdateSeek()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentPlayerBinding = FragmentPlayerBinding.bind(view)
        binding = fragmentPlayerBinding

        initPlayView(fragmentPlayerBinding)
        initPlayControlButtons(fragmentPlayerBinding)
        initRecyclerView(fragmentPlayerBinding)
        initSeekbar(fragmentPlayerBinding)
        initPlayListButton(fragmentPlayerBinding)

        getVideoListFromSever()
    }

    private fun initSeekbar(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.playerSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                // seekbar을 이동할때 호출되는 함수
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // seekbar에 손을 뗐을때
                player?.seekTo((seekBar.progress*1000).toLong())
            }

        })
        fragmentPlayerBinding.playlistSeekbar.setOnTouchListener { view, motionEvent ->  false }
    }

    private fun initPlayControlButtons(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.playControlImageView.setOnClickListener { // 플레이 버튼을 눌렀을때
            val player = this.player ?: return@setOnClickListener
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }

        }
        fragmentPlayerBinding.skipNextImageView.setOnClickListener {    // next 버튼을 눌렀을때
            val nextMusic = model.nextMusic() ?: return@setOnClickListener
            playMusic(nextMusic)
        }
        fragmentPlayerBinding.skipPrevImageView.setOnClickListener {    // prev 버튼을 눌렀을때
            val prevMusic = model.prevMusic() ?: return@setOnClickListener
            playMusic(prevMusic)
        }
    }

    private fun initPlayView(fragmentPlayerBinding: FragmentPlayerBinding) {
        context?.let {
            player = SimpleExoPlayer.Builder(it).build()
        }
        fragmentPlayerBinding.playerView.player = player

        binding?.let { binding ->
            player?.addListener(object : Player.EventListener {
                override fun onIsPlayingChanged(isPlaying: Boolean) { // 플레이어가 재생이되거나 멈출때 내려오면 실행되는 함수
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying) {
                        binding.playControlImageView.setImageResource(R.drawable.ic_baseline_pause_48)
                    } else {
                        binding.playControlImageView.setImageResource(R.drawable.ic_baseline_play_arrow_48)
                    }
                }

                override fun onPlaybackStateChanged(state: Int) { // 플레이어의 상태가 바뀔때 내려오는 콜백함수
                    super.onPlaybackStateChanged(state)
                    UpdateSeek()
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) { // 미디어 아이템이 바뀔때
                    super.onMediaItemTransition(mediaItem, reason)
                    val newIndex = mediaItem?.mediaId ?: return
                    model.currentposition = newIndex.toInt()
                    updatePlayerView(model.currentMusicModel())
                    playListAdapter.submitList(model.getAdapterModels())
                }
            })
        }
    }

    private fun UpdateSeek() {
        val player  = this.player ?: return
        val duration = if (player.duration >= 0) player.duration else 0
        val position = player.contentPosition

        updateSeekUI(duration, position)

        val state = player.playbackState

        view?.removeCallbacks(updateSeekRunnalbe)
        if (state != Player.STATE_IDLE && state != Player.STATE_ENDED){
            view?.postDelayed(updateSeekRunnalbe, 1000)
        }

    }

    private fun updateSeekUI(duration: Long, position: Long) {
        binding?.let { binding ->
            binding.playlistSeekbar.max = (duration/1000).toInt()
            binding.playlistSeekbar.progress = (position/1000).toInt()

            binding.playerSeekbar.max = (duration/1000).toInt()
            binding.playerSeekbar.progress = (position/1000).toInt()

            binding.playTimeTextView.text = String.format("%02d:%02d",TimeUnit.MINUTES.convert(position, TimeUnit.MILLISECONDS), (position/1000)%60)
            binding.totalTimeTextView.text =String.format("%02d:%02d",TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS), (duration/1000)%60)
        }
    }

    private fun updatePlayerView(currentMusicModel: MusicModel?) {
        currentMusicModel ?: return

        binding?.let { binding ->
            binding.trackTextView.text = currentMusicModel.track
            binding.artistTextView.text = currentMusicModel.artist
            Glide.with(binding.coverImageView.context)
                .load(currentMusicModel.coverUrl)
                .into(binding.coverImageView)
        }

    }

    private fun initRecyclerView(fragmentPlayerBinding: FragmentPlayerBinding) {
        playListAdapter = PlayListAdapter {
            // 음악 재생
            playMusic(it)
        }
        fragmentPlayerBinding.playlistRecyclerView.apply {
            adapter = playListAdapter
            layoutManager = LinearLayoutManager(context)
        }

    }

    private fun initPlayListButton(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.playlistImageView.setOnClickListener {
            if (model.currentposition == -1)return@setOnClickListener
            fragmentPlayerBinding.playerViewGroup.isVisible = model.iswatchPlaylistView
            fragmentPlayerBinding.playListViewGroup.isVisible = model.iswatchPlaylistView.not()

            model.iswatchPlaylistView = !model.iswatchPlaylistView
        }
    }

    private fun getVideoListFromSever() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(MusicService::class.java)
            .also {
                it.listMusics()
                    .enqueue(object : Callback<MusicDto> {
                        override fun onResponse(
                            call: Call<MusicDto>,
                            response: Response<MusicDto>
                        ) {
                            Log.d("playerFragment", "${response.body()}")

                            response.body()?.let { musicDto ->

                                model = musicDto.mapper()

                                setMusicList(model.getAdapterModels())
                                playListAdapter.submitList(model.getAdapterModels())
                            }
                        }

                        override fun onFailure(call: Call<MusicDto>, t: Throwable) {

                        }

                    })
            }
    }

    private fun setMusicList(modelList: List<MusicModel>) {
        context?.let {
            player?.addMediaItems(modelList.map { musicModel ->
                MediaItem.Builder().setMediaId(musicModel.id.toString())
                    .setUri(musicModel.streamUrl).build()
            })
            player?.prepare()

        }
    }
    private fun playMusic(musicModel: MusicModel){
        model.updateCurrentPosition(musicModel)
        player?.seekTo(model.currentposition, 0)
        player?.play()
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
        view?.removeCallbacks(updateSeekRunnalbe)
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        view?.removeCallbacks(updateSeekRunnalbe)
    }

    companion object {
        fun newInstance(): PlayerFragment {
            return PlayerFragment()
        }
    }

}