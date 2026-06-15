package market.femi

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import androidx.core.net.toUri

data class SplashPage(
    val eyebrow: String,
    val title: String,
    val subtitle: String,
    val cta: String,
    val ctaIcon: ImageVector
)

private val pages = listOf(
    SplashPage(
        eyebrow = "Welcome",
        title = "A new home\nfor music.",
        subtitle = "Watch artists drop their best work, in motion.",
        cta = "Continue",
        ctaIcon = Icons.AutoMirrored.Rounded.ArrowForward
    ),
    SplashPage(
        eyebrow = "Listen",
        title = "Hear it.\nFeel it.\nOwn it.",
        subtitle = "From the first listen to the lyric in your hand.",
        cta = "Continue",
        ctaIcon = Icons.AutoMirrored.Rounded.ArrowForward
    ),
    SplashPage(
        eyebrow = "Begin",
        title = "Step into\nthe rhythm.",
        subtitle = "Made for fans. Built for artists.",
        cta = "Get started",
        ctaIcon = Icons.Rounded.Star
    )
)

@Composable
fun Splash(onContinue: () -> Unit) {
    val isPreview = androidx.compose.ui.platform.LocalInspectionMode.current

    var step by remember { mutableIntStateOf(0) }
    var titleIn by remember { mutableStateOf(isPreview) }
    var subtitleIn by remember { mutableStateOf(isPreview) }
    var ctaIn by remember { mutableStateOf(isPreview) }

    val page = pages[step]

    // Sequence animations when step changes
    LaunchedEffect(step) {
        if (isPreview) return@LaunchedEffect
        titleIn = false
        subtitleIn = false
        ctaIn = false
        
        delay(120)
        titleIn = true
        delay(140)
        subtitleIn = true
        delay(120)
        ctaIn = true
    }

    val context = LocalContext.current
    val exoPlayer = remember {
        if (isPreview) null else {
            ExoPlayer.Builder(context).build().apply {
                val uri = "android.resource://${context.packageName}/${R.raw.splash_video1}".toUri()
                setMediaItem(MediaItem.fromUri(uri))
                repeatMode = Player.REPEAT_MODE_ALL
                playWhenReady = true
                prepare()
                volume = 0f
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer?.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (isPreview) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1A1A24),
                                Color(0xFF0F0F15),
                                Color(0xFF050508)
                            )
                        )
                    )
            )
        } else {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    PlayerView(context).apply {
                        player = exoPlayer
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    }
                }
            )
        }

        // Overlay gradient for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.55f),
                            Color.Black.copy(alpha = 0.05f),
                            Color.Black.copy(alpha = 0.35f),
                            Color.Black.copy(alpha = 0.9f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ProgressDots(
                current = step,
                total = pages.size,
                modifier = Modifier
                    .padding(top = 64.dp) // Status bar padding approx
                    .align(Alignment.CenterHorizontally)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp) // Navigation bar padding approx
            ) {
                StepCopy(
                    page = page,
                    titleIn = titleIn,
                    subtitleIn = subtitleIn,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 32.dp)
                )

                ContinueButton(
                    page = page,
                    ctaIn = ctaIn,
                    onClick = {
                        if (step == pages.size - 1) {
                            onContinue()
                        } else {
                            // Reset state for out-animation
                            titleIn = false
                            subtitleIn = false
                            ctaIn = false
                            step += 1
                        }
                    },
                    modifier = Modifier
                        .padding(horizontal = 28.dp)
                        .padding(bottom = 24.dp)
                )
            }
        }
    }
}

@Composable
private fun StepCopy(
    page: SplashPage,
    titleIn: Boolean,
    subtitleIn: Boolean,
    modifier: Modifier = Modifier
) {
    val titleAlpha by animateFloatAsState(
        targetValue = if (titleIn) 1f else 0f,
        animationSpec = tween(550), label = "titleAlpha"
    )
    val titleOffset by animateDpAsState(
        targetValue = if (titleIn) 0.dp else 22.dp,
        animationSpec = tween(550), label = "titleOffset"
    )
    val titleBlur by animateDpAsState(
        targetValue = if (titleIn) 0.dp else 10.dp,
        animationSpec = tween(550), label = "titleBlur"
    )

    val subtitleAlpha by animateFloatAsState(
        targetValue = if (subtitleIn) 1f else 0f,
        animationSpec = tween(550), label = "subtitleAlpha"
    )
    val subtitleOffset by animateDpAsState(
        targetValue = if (subtitleIn) 0.dp else 16.dp,
        animationSpec = tween(550), label = "subtitleOffset"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Eyebrow with glass effect
        Box(
            modifier = Modifier
                .offset(y = if (titleIn) 0.dp else 12.dp)
                .alpha(titleAlpha)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.15f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = page.eyebrow.uppercase(),
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.4.sp
            )
        }

        // Title
        Text(
            text = page.title,
            color = Color.White,
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 48.sp,
            modifier = Modifier
                .offset(y = titleOffset)
                .alpha(titleAlpha)
                .blur(radius = titleBlur)
        )

        // Subtitle
        Text(
            text = page.subtitle,
            color = Color.White.copy(alpha = 0.88f),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 28.sp,
            modifier = Modifier
                .offset(y = subtitleOffset)
                .alpha(subtitleAlpha)
        )
    }
}

@Composable
private fun ContinueButton(
    page: SplashPage,
    ctaIn: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ctaAlpha by animateFloatAsState(
        targetValue = if (ctaIn) 1f else 0f,
        animationSpec = tween(550), label = "ctaAlpha"
    )
    val ctaOffset by animateDpAsState(
        targetValue = if (ctaIn) 0.dp else 24.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow), label = "ctaOffset"
    )
    val ctaScale by animateFloatAsState(
        targetValue = if (ctaIn) 1f else 0.94f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow), label = "ctaScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = ctaOffset)
            .alpha(ctaAlpha)
            .scale(ctaScale)
            .clip(RoundedCornerShape(100.dp))
            .background(Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = page.cta,
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = page.ctaIcon,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ProgressDots(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until total) {
            val width by animateDpAsState(
                targetValue = if (i == current) 28.dp else 8.dp,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "dotWidth"
            )
            val alpha by animateFloatAsState(
                targetValue = if (i == current) 0.95f else 0.35f,
                animationSpec = tween(300),
                label = "dotAlpha"
            )

            Box(
                modifier = Modifier
                    .size(width = width, height = 6.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = alpha))
            )
        }
    }
}

@Preview
@Composable
fun SplashPreview() {
    Splash(onContinue = {})
}
