package com.bookstream.app.ui

import androidx.compose.ui.graphics.Color

data class BookItem(
    val id: String, val title: String, val author: String, val category: String,
    val duration: String, val synopsis: String, val fullStoryText: String,
    val coverColor: Color, val icon: String, val languageTag: String = "hi-IN"
)

/** The narration strings are original summaries/intros, not full copyrighted editions. */
object BookData {
    val catalog = listOf(
        BookItem("godan","Godan","Munshi Premchand","Novel · Upanyas","12 hr 20 min",
            "Hori's longing for a cow becomes a moving portrait of rural debt, dignity and inequality.",
            "Welcome to BookStream. Today we explore Godan by Munshi Premchand. At the heart of this classic is Hori, a poor farmer who dreams of owning a cow, a symbol of prosperity and respect in his village. His family faces debt, social pressure and difficult choices. Through Hori and those around him, Premchand observes compassion, ambition, hypocrisy and the dignity of ordinary people. This is an original summary, not the complete novel.",
            Color(0xFF76523D),"📖"),
        BookItem("gaban","Gaban","Munshi Premchand","Novel · Upanyas","9 hr 10 min",
            "A social novel exploring vanity, financial pressure and the consequences of maintaining appearances.",
            "Gaban by Munshi Premchand examines the pressure to appear wealthy and successful. Ramanath wants to satisfy expectations surrounding him, especially those connected with jewellery and status. Small compromises grow into serious trouble, and the story asks how fear and pride can trap a person. This original introduction reflects on honesty, responsibility and the cost of appearances.",
            Color(0xFF526C83),"📚"),
        BookItem("nirmala","Nirmala","Munshi Premchand","Novel · Upanyas","8 hr 45 min",
            "A young woman's life is shaped by unequal marriage customs, social pressure and suspicion.",
            "Nirmala portrays the harm caused by dowry pressure, mismatched marriage and mistrust. A young woman's hopes are narrowed by decisions made around her, while suspicion steadily damages a family. The novel asks readers to look beyond individual blame toward customs and expectations that leave people vulnerable. This is an original summary, not a full reproduction of the novel.",
            Color(0xFF8A5B68),"🌹"),
        BookItem("rashmirathi","Rashmirathi","Ramdhari Singh Dinkar","Poetry · Kavitayein","3 hr 40 min",
            "A celebrated epic poem retelling Karna's story through honour, identity and justice.",
            "Rashmirathi by Ramdhari Singh Dinkar brings Karna to the centre of the Mahabharata's moral world. Born with extraordinary gifts but denied easy acceptance, Karna navigates loyalty, honour and social injustice. Dinkar invites us to consider whether worth should be judged by birth or by courage and generosity. This original introduction discusses themes without reproducing the poem's verses.",
            Color(0xFF9B542E),"☀️"),
        BookItem("madhushala","Madhushala","Harivansh Rai Bachchan","Poetry · Ghazal","2 hr 15 min",
            "A landmark Hindi poetic work using the tavern as a symbol for life, fellowship and meaning.",
            "Madhushala by Harivansh Rai Bachchan is famous for its recurring images of the cup, the traveller and the tavern. These images can suggest joy, mortality, fellowship and the many paths people take toward meaning. Its musical rhythm helped make it one of modern Hindi poetry's best-known works. This original introduction explores symbolism without quoting protected verses.",
            Color(0xFF7C3E4C),"🍷"),
        BookItem("mahadevi","Selected Life & Poetry","Mahadevi Verma","Poetry · Essays","2 hr 30 min",
            "An introduction to the Chhayavaad poet and essayist, known for lyrical inwardness and compassion.",
            "Mahadevi Verma was a major voice in modern Hindi literature. Her poetry often turns toward longing, solitude, nature and the search for an inner light. Her prose reflects compassion for living beings and attention to women's lives. This original biographical narration introduces broad themes and her lasting place in Hindi letters.",
            Color(0xFF566D85),"🪷"),
        BookItem("idgah","Idgah","Munshi Premchand","Short Story · Kahani","25 min",
            "A young boy's thoughtful choice at a village fair becomes a story about love and sacrifice.",
            "At a village fair, a young boy named Hamid has only a few coins to spend. While other children choose sweets and toys, he thinks about the pain his grandmother feels when handling hot utensils. His decision to buy a practical gift reveals a tenderness money cannot measure. Idgah endures because it shows how a child's love can turn a small object into a profound act of care. This is an original retelling.",
            Color(0xFF527A63),"🧒"),
        BookItem("kafan","Kafan","Munshi Premchand","Short Story · Kahani","35 min",
            "A stark social story about poverty, hunger and moral numbness under relentless deprivation.",
            "Kafan by Munshi Premchand is one of the most unsettling stories in Hindi literature. Through Ghisu and Madhav, it depicts extreme poverty and a world in which repeated suffering has distorted ordinary responses to grief. Its painful irony asks how deprivation can erode dignity and compassion. This is a thematic narration, not the original story text.",
            Color(0xFF57515C),"🌑"),
        BookItem("namak","Namak Ka Daroga","Munshi Premchand","Short Story · Kahani","30 min",
            "An honest official resists bribery and discovers integrity may be valued in unexpected ways.",
            "In Namak Ka Daroga, an upright salt inspector faces pressure from wealth and influence. His refusal to accept a bribe costs him comfort and status, yet his honesty leaves a lasting impression. The story contrasts official power with moral courage and asks what integrity is worth in a society tempted by corruption.",
            Color(0xFF756A45),"⚖️"),
        BookItem("tirichh","Tirichh — Story Introduction","Uday Prakash","Modern Short Fiction","20 min",
            "An introduction to Uday Prakash's modern short fiction and its unsettling social atmosphere.",
            "Uday Prakash is known for modern Hindi fiction that blends everyday detail with unease, memory and social criticism. This original introduction places Tirichh within a wider discussion of how contemporary stories can reveal invisible structures of fear and power. It does not reproduce the copyrighted story.",
            Color(0xFF4D6475),"🌒"),
        BookItem("tagore","Selected Stories & Poems","Rabindranath Tagore","World Literature","2 hr 50 min",
            "A doorway into Tagore's humanist writing, rich with nature, longing, family and freedom of spirit.",
            "Rabindranath Tagore was a poet, novelist, composer and thinker whose work crossed linguistic and cultural boundaries. His writing explores the relationship between individuals and society, the natural world, and human dignity. This original introduction offers a starting point for exploring his legacy without reproducing protected translations.",
            Color(0xFF426B62),"🌿","en-IN"),
        BookItem("wings","Wings of Fire — Overview","A. P. J. Abdul Kalam","Biography","45 min",
            "A concise introduction to the life, scientific work and public service of India's former president.",
            "A. P. J. Abdul Kalam's life is remembered as a journey from a modest childhood in Rameswaram to contributions to India's aerospace programmes and later the presidency. His story highlights curiosity, teamwork, discipline and education. This original overview introduces key themes without reproducing his autobiography.",
            Color(0xFF466A91),"🚀","en-IN"),
        BookItem("bhagat","Bhagat Singh: A Life","Biographical overview","Biography · History","40 min",
            "An introductory portrait of the revolutionary whose writings became part of India's independence movement.",
            "Bhagat Singh became a defining figure in India's anticolonial struggle. His writings show a young thinker concerned with freedom, justice, political responsibility and ideas. This original biographical introduction places his life in historical context and encourages further study from reliable historical sources.",
            Color(0xFF866047),"✊","en-IN"),
        BookItem("deepwork","Deep Work — Overview","Cal Newport","Business · Productivity","35 min",
            "A practical overview of focused work, distraction management and sustained concentration.",
            "This original overview introduces the central idea associated with Deep Work: focused, distraction-resistant effort is valuable in a world full of interruptions. It suggests protecting blocks of concentration, setting clear goals and building routines that make demanding work easier to begin. This is a summary, not a substitute for the book's full text.",
            Color(0xFF385B7C),"🎯","en-IN")
    )
    fun find(id: String): BookItem? = catalog.firstOrNull { it.id == id }
}
