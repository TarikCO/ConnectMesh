package com.connectmesh.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.connectmesh.ConnectMeshApplication
import com.connectmesh.domain.model.UserProfile
import com.connectmesh.ui.auth.AuthViewModel
import com.connectmesh.ui.profile.ProfileViewModel
import com.connectmesh.ui.theme.Coral
import com.connectmesh.ui.theme.Forest
import com.connectmesh.ui.theme.Ink
import com.connectmesh.ui.theme.Mint
import com.connectmesh.ui.theme.Sand

private enum class Destination(val route: String, val label: String, val mark: String) {
    Home("home", "Home", "⌂"), Connect("connect", "Connect", "＋"),
    Network("network", "Network", "◎"), Profile("profile", "Profile", "◉")
}

data class Connection(val name: String, val headline: String, val initials: String, val color: Color, val whenMet: String)

private val sampleConnections = listOf(
    Connection("Arthur Silva", "ML Engineer", "AS", Color(0xFFCEA8FF), "Today · HackUSF"),
    Connection("Natalia Costa", "CS @ USF", "NC", Color(0xFFFFC98B), "Today · HackUSF"),
    Connection("Julia Souza", "Software Engineer", "JS", Color(0xFF8EDAD4), "Yesterday · BRASA"),
    Connection("Isa Oliveira", "Data Scientist", "IO", Color(0xFFF7A6B3), "Yesterday · HackUSF")
)

@Composable
fun ConnectMeshApp() {
    val app = LocalContext.current.applicationContext as ConnectMeshApplication
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.factory(app.authRepository))
    val auth by authViewModel.state.collectAsState()
    if (auth.loading) return Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) { Text("Loading ConnectMesh…") }
    val currentUser = auth.user ?: return AuthScreen(onSubmit = authViewModel::submit)
    val profileViewModel: ProfileViewModel = viewModel(key = currentUser.id, factory = ProfileViewModel.factory(currentUser.id, app.profileRepository))
    val profile by profileViewModel.profile.collectAsState()
    AuthenticatedApp(profile = profile, onSaveProfile = profileViewModel::save, onSignOut = authViewModel::signOut)
}

@Composable
private fun AuthenticatedApp(profile: UserProfile?, onSaveProfile: (UserProfile) -> Unit, onSignOut: () -> Unit) {
    val navController = rememberNavController()
    val entry by navController.currentBackStackEntryAsState()
    val currentRoute = entry?.destination?.route
    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                Destination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = { navController.navigate(destination.route) { popUpTo(navController.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true } },
                        icon = { Text(destination.mark, fontSize = 23.sp, fontWeight = FontWeight.Bold) },
                        label = { Text(destination.label) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = Forest, selectedTextColor = Forest, indicatorColor = Mint)
                    )
                }
            }
        }
    ) { padding ->
        NavHost(navController, startDestination = Destination.Home.route, modifier = Modifier.padding(padding)) {
            composable(Destination.Home.route) { HomeScreen(profile?.name ?: "Tarik") { navController.navigate(Destination.Connect.route) } }
            composable(Destination.Connect.route) { ConnectScreen() }
            composable(Destination.Network.route) { NetworkScreen() }
            composable(Destination.Profile.route) { ProfileScreen(profile, onSaveProfile, onSignOut) }
        }
    }
}

@Composable private fun HomeScreen(name: String, onConnect: () -> Unit) = ScreenColumn {
    Text("Good afternoon, $name  👋", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Text("Make your next introduction count.", color = Color(0xFF63706B))
    Spacer(Modifier.height(20.dp))
    EventPill()
    Spacer(Modifier.height(18.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        Metric("12", "connections\ntoday", Modifier.weight(1f))
        Metric("8", "people\nnearby", Modifier.weight(1f), Coral)
    }
    Spacer(Modifier.height(22.dp))
    Text("Suggested for you", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(10.dp))
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(0.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Avatar("LA", Color(0xFFFFC98B), 52)
            Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) {
                Text("Lucas Almeida", fontWeight = FontWeight.Bold)
                Text("Android Developer", color = Color(0xFF63706B))
                Text("Kotlin  ·  Cloud  ·  Startups", color = Forest, fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp))
            }
            Text("92%", color = Forest, fontWeight = FontWeight.Bold)
        }
    }
    Spacer(Modifier.height(24.dp))
    Button(onClick = onConnect, modifier = Modifier.fillMaxWidth().height(116.dp), shape = RoundedCornerShape(28.dp), colors = ButtonDefaults.buttonColors(containerColor = Forest)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("TAP", fontSize = 32.sp, fontWeight = FontWeight.Black)
            Text("Ready to connect", fontSize = 14.sp)
        }
    }
    Spacer(Modifier.height(14.dp))
    Text("NFC  ·  QR code  ·  Nearby device", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Color(0xFF63706B), fontSize = 13.sp)
}

@Composable private fun ConnectScreen() = ScreenColumn {
    Text("Connect", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Text("Turn a real-world hello into a lasting connection.", color = Color(0xFF63706B))
    Spacer(Modifier.height(28.dp))
    Box(Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(32.dp)).background(Forest), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(176.dp).background(Color.White, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) { QrPattern() }
            Spacer(Modifier.height(16.dp)); Text("Your connection code", color = Color.White, fontWeight = FontWeight.Bold)
            Text("Let someone scan to connect", color = Mint, fontSize = 13.sp)
        }
    }
    Spacer(Modifier.height(24.dp)); Text("Choose a method", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(10.dp))
    ConnectionMethod("▣", "Scan a QR code", "Point your camera at their code")
    ConnectionMethod("⌁", "Tap with NFC", "Hold devices or an NFC card together")
    ConnectionMethod("⌁", "Discover nearby", "Find people actively sharing nearby")
}

@Composable private fun NetworkScreen() = ScreenColumn {
    var query by remember { mutableStateOf("") }
    Text("Your network", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Text("16 people, 4 fields, one growing community.", color = Color(0xFF63706B))
    Spacer(Modifier.height(18.dp))
    TextField(query, { query = it }, placeholder = { Text("Search connections") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(16.dp), colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent))
    Spacer(Modifier.height(18.dp))
    NetworkMiniGraph()
    Spacer(Modifier.height(18.dp)); Text("Recent connections", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    val filtered = sampleConnections.filter { it.name.contains(query, true) || it.headline.contains(query, true) }
    filtered.forEach { person -> PersonRow(person) }
}

@Composable private fun ProfileScreen(profile: UserProfile?, onSave: (UserProfile) -> Unit, onSignOut: () -> Unit) = ScreenColumn {
    var editing by remember { mutableStateOf(profile == null) }
    var name by remember(profile) { mutableStateOf(profile?.name ?: "") }
    var headline by remember(profile) { mutableStateOf(profile?.headline ?: "") }
    var bio by remember(profile) { mutableStateOf(profile?.bio ?: "") }
    var skills by remember(profile) { mutableStateOf(profile?.skills?.joinToString(", ") ?: "") }
    var interests by remember(profile) { mutableStateOf(profile?.interests?.joinToString(", ") ?: "") }
    if (editing) {
        Text(if (profile == null) "Create your profile" else "Edit profile", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("This stays on your device until you wire Firebase.", color = Color(0xFF63706B), modifier = Modifier.padding(top = 4.dp))
        Spacer(Modifier.height(20.dp))
        ProfileField("Name", name) { name = it }
        ProfileField("Headline", headline) { headline = it }
        ProfileField("About you", bio, singleLine = false) { bio = it }
        ProfileField("Skills", skills) { skills = it }
        ProfileField("Interests", interests) { interests = it }
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            onSave(UserProfile(name = name.trim(), headline = headline.trim(), bio = bio.trim(), skills = skills.tags(), interests = interests.tags()))
            editing = false
        }, enabled = name.isNotBlank(), modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) { Text("Save profile") }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(onSignOut, Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(16.dp)) { Text("Sign out") }
        return@ScreenColumn
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Avatar(profile?.name.initials(), Mint, 74); Spacer(Modifier.width(16.dp)); Column { Text(profile?.name.orEmpty(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text(profile?.headline.orEmpty(), color = Color(0xFF63706B)); Text("Your ConnectMesh profile", color = Forest, fontSize = 13.sp) }
    }
    Spacer(Modifier.height(20.dp)); EventPill()
    Spacer(Modifier.height(20.dp)); Text("42", fontSize = 36.sp, fontWeight = FontWeight.Black); Text("connections made", color = Color(0xFF63706B))
    Spacer(Modifier.height(24.dp)); Text("About me", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text(profile?.bio.orEmpty(), modifier = Modifier.padding(top = 7.dp), color = Color(0xFF43504B))
    Spacer(Modifier.height(22.dp)); Text("Interested in", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); TagRow(profile?.interests.orEmpty())
    Spacer(Modifier.height(18.dp)); Text("Skills", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); TagRow(profile?.skills.orEmpty())
    Spacer(Modifier.height(28.dp)); OutlinedButton({ editing = true }, Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) { Text("Edit profile") }
    Spacer(Modifier.height(10.dp)); Text("Sign out", color = Forest, modifier = Modifier.fillMaxWidth().clickable(onClick = onSignOut).padding(12.dp), textAlign = TextAlign.Center)
}

@Composable private fun AuthScreen(onSubmit: (String, String, Boolean) -> Unit) {
    var email by remember { mutableStateOf("") }; var password by remember { mutableStateOf("") }; var register by remember { mutableStateOf(false) }; var error by remember { mutableStateOf<String?>(null) }
    Box(Modifier.fillMaxSize().background(Forest).padding(24.dp), contentAlignment = Alignment.Center) {
        Column(Modifier.fillMaxWidth()) {
            Text("ConnectMesh", color = Color.White, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black)
            Text("Turn real-world encounters into lasting connections.", color = Mint, modifier = Modifier.padding(top = 6.dp))
            Spacer(Modifier.height(34.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(28.dp)) {
                Column(Modifier.padding(20.dp)) {
                    Text(if (register) "Create your account" else "Welcome back", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp)); ProfileField("Email", email) { email = it }; ProfileField("Password (6+ characters)", password) { password = it }
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(top = 10.dp)) }
                    Button(onClick = {
                        error = when { !email.contains('@') -> "Enter a valid email address."; password.length < 6 -> "Password must be at least 6 characters."; else -> null }
                        if (error == null) onSubmit(email, password, register)
                    }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), shape = RoundedCornerShape(14.dp)) { Text(if (register) "Create account" else "Sign in") }
                    Text(if (register) "Already have an account? Sign in" else "New here? Create an account", color = Forest, modifier = Modifier.fillMaxWidth().clickable { register = !register }.padding(top = 14.dp), textAlign = TextAlign.Center, fontSize = 13.sp)
                }
            }
            Text("Development mode · Firebase is not connected", color = Mint, fontSize = 12.sp, modifier = Modifier.fillMaxWidth().padding(top = 18.dp), textAlign = TextAlign.Center)
        }
    }
}

@Composable private fun ProfileField(label: String, value: String, singleLine: Boolean = true, onChange: (String) -> Unit) {
    Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 10.dp, bottom = 4.dp))
    TextField(value, onChange, modifier = Modifier.fillMaxWidth(), singleLine = singleLine, minLines = if (singleLine) 1 else 3, shape = RoundedCornerShape(12.dp), colors = TextFieldDefaults.colors(unfocusedContainerColor = Sand, focusedContainerColor = Sand, unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent))
}

private fun String.tags() = split(',').map(String::trim).filter(String::isNotBlank)
private fun String?.initials() = this.orEmpty().split(' ').filter(String::isNotBlank).take(2).joinToString("") { it.first().uppercase() }.ifBlank { "CM" }

@Composable private fun ScreenColumn(content: @Composable () -> Unit) = LazyColumn(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 20.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 24.dp)) { item { content() } }

@Composable private fun EventPill() = Row(Modifier.clip(RoundedCornerShape(50)).background(Mint).padding(horizontal = 12.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) { Text("●", color = Forest, fontSize = 11.sp); Spacer(Modifier.width(6.dp)); Text("HackUSF 2026", color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
@Composable private fun Metric(value: String, label: String, modifier: Modifier, accent: Color = Forest) = Card(modifier, colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(0.dp)) { Column(Modifier.padding(16.dp)) { Text(value, fontSize = 30.sp, fontWeight = FontWeight.Black, color = accent); Text(label, color = Color(0xFF63706B), fontSize = 13.sp) } }
@Composable private fun Avatar(initials: String, color: Color, size: Int) = Box(Modifier.size(size.dp).clip(CircleShape).background(color), contentAlignment = Alignment.Center) { Text(initials, fontWeight = FontWeight.Bold, color = Ink) }
@Composable private fun ConnectionMethod(symbol: String, title: String, detail: String) = Card(Modifier.fillMaxWidth().padding(vertical = 5.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(0.dp)) { Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(Sand), contentAlignment = Alignment.Center) { Text(symbol, fontSize = 22.sp, color = Forest) }; Spacer(Modifier.width(13.dp)); Column { Text(title, fontWeight = FontWeight.Bold); Text(detail, color = Color(0xFF63706B), fontSize = 12.sp) } } }
@Composable private fun PersonRow(person: Connection) = Row(Modifier.fillMaxWidth().padding(vertical = 12.dp).clickable { }, verticalAlignment = Alignment.CenterVertically) { Avatar(person.initials, person.color, 48); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(person.name, fontWeight = FontWeight.Bold); Text(person.headline, color = Color(0xFF63706B), fontSize = 13.sp) }; Text(person.whenMet, color = Color(0xFF63706B), fontSize = 11.sp, textAlign = TextAlign.End) }
@Composable private fun TagRow(tags: List<String>) = Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) { tags.take(3).forEach { Text(it, Modifier.clip(RoundedCornerShape(20.dp)).background(Sand).padding(horizontal = 10.dp, vertical = 7.dp), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) } }

@Composable private fun QrPattern() = Canvas(Modifier.size(140.dp)) { val cell = size.width / 7; for (x in 0..6) for (y in 0..6) { val finder = (x < 2 && y < 2) || (x > 4 && y < 2) || (x < 2 && y > 4); if (finder || (x * 3 + y * 5) % 4 == 0) drawRect(Ink, topLeft = androidx.compose.ui.geometry.Offset(x * cell, y * cell), size = androidx.compose.ui.geometry.Size(cell - 3, cell - 3)) } }
@Composable private fun NetworkMiniGraph() = Card(Modifier.fillMaxWidth().height(180.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(0.dp)) { Canvas(Modifier.fillMaxSize().padding(14.dp)) { val c = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f); val points = listOf(c, androidx.compose.ui.geometry.Offset(size.width * .18f, size.height * .25f), androidx.compose.ui.geometry.Offset(size.width * .79f, size.height * .22f), androidx.compose.ui.geometry.Offset(size.width * .25f, size.height * .77f), androidx.compose.ui.geometry.Offset(size.width * .78f, size.height * .72f)); points.drop(1).forEach { drawLine(Color(0xFFBFD3CA), c, it, 3f) }; points.forEachIndexed { i, p -> drawCircle(if (i == 0) Forest else listOf(Coral, Color(0xFFCEA8FF), Color(0xFF8EDAD4), Color(0xFFFFC98B))[i - 1], if (i == 0) 22f else 16f, p); drawCircle(Color.White, if (i == 0) 22f else 16f, p, style = Stroke(2f)) } } }
