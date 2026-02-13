import React, { useState, useEffect } from "react";
import { motion, AnimatePresence } from "framer-motion";
import {
  Trophy,
  Sword,
  Zap,
  Brain,
  Dumbbell,
  Plus,
  ShieldCheck,
  Clock,
  Gift,
  Skull,
  Edit3,
  Briefcase,
  Heart,
  Star,
  Lock,
  Crown,
  Flame,
} from "lucide-react";

const getXpNeeded = (lvl) => Math.floor(100 * Math.pow(lvl, 1.5));

const categoryIcons = {
  Estudo: <Brain size={20} />,
  Saúde: <Heart size={20} />,
  Academia: <Dumbbell size={20} />,
  Trabalho: <Briefcase size={20} />,
  Outros: <Zap size={20} />,
};

// Títulos de Conquistas com peso
const achievements = [
  { lvl: 10, title: "O Desperto", subtitle: "Primeiros passos na maestria" },
  {
    lvl: 20,
    title: "Guerreiro de Elite",
    subtitle: "Constância inabalável provada",
  },
  {
    lvl: 30,
    title: "Mestre da Disciplina",
    subtitle: "Sua vontade dobra a realidade",
  },
  { lvl: 40, title: "Grão-Mestre", subtitle: "Referência em produtividade" },
  { lvl: 50, title: "Lenda Imortal", subtitle: "O ápice do potencial humano" },
];

export default function App() {
  const [player, setPlayer] = useState(() => {
    const saved = localStorage.getItem("lifexp_player");
    return saved
      ? JSON.parse(saved)
      : {
          name: "Mathzss",
          level: 1,
          xp: 0,
          streak: 0,
          tasks: [],
          inventory: [],
          lastLogin: new Date().toDateString(),
        };
  });

  const [showLevelUp, setShowLevelUp] = useState(false);
  const [showPenalty, setShowPenalty] = useState(false);
  const [isEditingName, setIsEditingName] = useState(false);

  const xpToNext = getXpNeeded(player.level);

  useEffect(() => {
    localStorage.setItem("lifexp_player", JSON.stringify(player));
  }, [player]);

  // RESET DIÁRIO
  useEffect(() => {
    const today = new Date().toDateString();
    if (player.lastLogin !== today) {
      if (player.tasks?.length > 0) {
        setShowPenalty(true);
        setPlayer((prev) => ({
          ...prev,
          xp: Math.max(0, prev.xp - prev.tasks.length * 50),
          tasks: [],
          streak: 0,
          lastLogin: today,
        }));
        setTimeout(() => setShowPenalty(false), 4000);
      } else {
        setPlayer((prev) => ({ ...prev, lastLogin: today }));
      }
    }
  }, [player.lastLogin, player.tasks]);

  // LEVEL UP
  useEffect(() => {
    if (player.xp >= xpToNext) {
      setShowLevelUp(true);
      const perks = [
        "Passe Livre 🍔",
        "Meditação 🧘",
        "Noite Gamer 🎮",
        "Cafeína Épica ☕",
      ];
      setPlayer((prev) => ({
        ...prev,
        level: prev.level + 1,
        xp: prev.xp - xpToNext,
        inventory: [
          ...(prev.inventory || []),
          {
            id: Date.now(),
            name: perks[Math.floor(Math.random() * perks.length)],
          },
        ],
      }));
      setTimeout(() => setShowLevelUp(false), 3000);
    }
  }, [player.xp, xpToNext]);

  const addTask = (e) => {
    e.preventDefault();
    const formData = new FormData(e.target);
    const diff = formData.get("difficulty");
    const xpValues = { Easy: 40, Medium: 100, Hard: 1000 };
    const newTask = {
      id: Date.now(),
      title: formData.get("title"),
      category: formData.get("category"),
      difficulty: diff,
      xp: xpValues[diff],
    };
    setPlayer((prev) => ({ ...prev, tasks: [newTask, ...(prev.tasks || [])] }));
    e.target.reset();
  };

  return (
    <div className="min-h-screen bg-[#050505] bg-[radial-gradient(circle_at_top_right,_var(--tw-gradient-stops))] from-indigo-950/20 via-black to-black text-zinc-100 p-4 md:p-10 font-sans">
      <AnimatePresence>
        {showLevelUp && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 z-[100] bg-black/80 backdrop-blur-xl flex items-center justify-center"
          >
            <motion.div
              initial={{ scale: 0.5 }}
              animate={{ scale: 1 }}
              className="text-center"
            >
              <Crown
                size={100}
                className="text-yellow-400 mx-auto mb-4 drop-shadow-[0_0_20px_rgba(250,204,21,0.6)]"
              />
              <h2 className="text-8xl font-black italic text-transparent bg-clip-text bg-gradient-to-r from-cyan-400 to-purple-500 underline decoration-cyan-500/30">
                LEVEL UP
              </h2>
              <p className="text-2xl font-bold mt-4 tracking-[0.5em] text-white/50">
                NÍVEL {player.level}
              </p>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

      <div className="max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-12 gap-10">
        {/* LADO ESQUERDO: STATUS E CONQUISTAS */}
        <aside className="lg:col-span-4 space-y-8">
          {/* CARD DE PERFIL GLASS */}
          <section className="bg-zinc-900/40 backdrop-blur-md p-8 rounded-[2.5rem] border border-white/10 shadow-2xl relative overflow-hidden">
            <div className="absolute -top-10 -right-10 w-32 h-32 bg-cyan-500/10 rounded-full blur-3xl" />

            <div className="flex items-center gap-6 mb-8">
              <div className="relative">
                <div className="w-20 h-20 bg-gradient-to-tr from-cyan-500 to-indigo-600 rounded-3xl flex items-center justify-center text-3xl font-black italic shadow-[0_0_30px_rgba(6,182,212,0.3)]">
                  {player.level}
                </div>
                <div className="absolute -bottom-2 -right-2 bg-black border border-white/10 p-1.5 rounded-lg">
                  <Flame className="text-orange-500" size={16} />
                </div>
              </div>
              <div>
                {isEditingName ? (
                  <input
                    autoFocus
                    className="bg-white/5 border-b-2 border-cyan-500 text-xl font-black outline-none w-full"
                    onBlur={(e) => {
                      setPlayer({ ...player, name: e.target.value });
                      setIsEditingName(false);
                    }}
                    defaultValue={player.name}
                  />
                ) : (
                  <h1
                    onClick={() => setIsEditingName(true)}
                    className="text-2xl font-black tracking-tight cursor-pointer hover:text-cyan-400 transition-colors flex items-center gap-2"
                  >
                    {player.name} <Edit3 size={16} className="text-white/20" />
                  </h1>
                )}
                <p className="text-[10px] font-black uppercase text-zinc-500 tracking-[0.3em] mt-1">
                  Status: Online
                </p>
              </div>
            </div>

            <div className="space-y-3">
              <div className="flex justify-between text-xs font-black uppercase italic">
                <span className="text-cyan-400">Progresso</span>
                <span className="text-zinc-500">
                  {player.xp} <span className="text-white/20">/</span>{" "}
                  {xpToNext} XP
                </span>
              </div>
              <div className="h-4 bg-black/50 rounded-full p-1 border border-white/5">
                <motion.div
                  className="h-full bg-gradient-to-r from-cyan-500 via-blue-500 to-purple-600 rounded-full shadow-[0_0_15px_rgba(6,182,212,0.4)]"
                  animate={{ width: `${(player.xp / xpToNext) * 100}%` }}
                />
              </div>
              <p className="text-[9px] text-center font-bold text-zinc-600 uppercase tracking-widest">
                Faltam {xpToNext - player.xp} XP para a próxima evolução
              </p>
            </div>
          </section>

          {/* MURAL DE CONQUISTAS TITAN */}
          <section className="bg-zinc-900/20 backdrop-blur-sm p-8 rounded-[2.5rem] border border-white/5">
            <h3 className="text-xs font-black uppercase tracking-[0.2em] mb-8 text-white/40 flex items-center gap-3">
              <Trophy size={18} className="text-yellow-500" /> Mural das Lendas
            </h3>
            <div className="space-y-4">
              {achievements.map((ach) => (
                <div
                  key={ach.lvl}
                  className={`group relative p-4 rounded-2xl border transition-all duration-500 ${
                    player.level >= ach.lvl
                      ? "border-cyan-500/30 bg-cyan-500/5"
                      : "border-white/5 bg-black/20 opacity-30"
                  }`}
                >
                  <div className="flex items-center gap-4">
                    <div
                      className={`w-10 h-10 rounded-lg flex items-center justify-center ${
                        player.level >= ach.lvl
                          ? "bg-cyan-500 text-black"
                          : "bg-white/5 text-zinc-500"
                      }`}
                    >
                      {player.level >= ach.lvl ? (
                        <Star size={20} fill="currentColor" />
                      ) : (
                        <Lock size={18} />
                      )}
                    </div>
                    <div>
                      <h4
                        className={`text-sm font-black uppercase italic ${
                          player.level >= ach.lvl
                            ? "text-white"
                            : "text-zinc-500"
                        }`}
                      >
                        {ach.title}
                      </h4>
                      <p className="text-[9px] font-bold text-zinc-600 uppercase tracking-tighter">
                        {ach.subtitle}
                      </p>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </section>
        </aside>

        {/* LADO DIREITO: QUESTS E INVENTARIO */}
        <main className="lg:col-span-8 space-y-8">
          {/* FORMULÁRIO DE ENTRADA ÉPICO */}
          <section className="bg-zinc-900/30 backdrop-blur-md p-2 rounded-[2rem] border border-white/10">
            <form
              onSubmit={addTask}
              className="grid grid-cols-1 md:grid-cols-6 gap-2"
            >
              <input
                name="title"
                required
                placeholder="Qual sua próxima conquista?"
                className="md:col-span-3 bg-black/40 border border-white/5 p-5 rounded-2xl outline-none focus:border-cyan-500/50 transition-all text-sm font-medium"
              />

              <select
                name="category"
                className="bg-black/40 border border-white/5 px-4 rounded-2xl text-[10px] font-black uppercase outline-none cursor-pointer"
              >
                {Object.keys(categoryIcons).map((cat) => (
                  <option key={cat} value={cat}>
                    {cat}
                  </option>
                ))}
              </select>

              <select
                name="difficulty"
                className="bg-black/40 border border-white/5 px-4 rounded-2xl text-[10px] font-black uppercase outline-none cursor-pointer"
              >
                <option value="Easy">Fácil</option>
                <option value="Medium">Médio</option>
                <option value="Hard">Difícil</option>
              </select>

              <button
                type="submit"
                className="bg-cyan-500 hover:bg-cyan-400 text-black font-black uppercase text-xs rounded-2xl transition-all shadow-[0_0_20px_rgba(6,182,212,0.2)]"
              >
                Aceitar
              </button>
            </form>
          </section>

          {/* LISTA DE QUESTS */}
          <div className="space-y-4">
            <div className="flex justify-between items-center px-4">
              <h2 className="text-sm font-black uppercase tracking-[0.3em] text-white/30 flex items-center gap-3">
                <Sword size={20} /> Missões Ativas
              </h2>
              <div className="flex items-center gap-2 text-[10px] font-black text-orange-500 italic bg-orange-500/10 px-3 py-1 rounded-full">
                <Flame size={14} /> Streak: {player.streak} Dias
              </div>
            </div>

            <AnimatePresence mode="popLayout">
              {player.tasks?.map((task) => (
                <motion.div
                  key={task.id}
                  layout
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ scale: 0.9, opacity: 0 }}
                  className="group bg-zinc-900/40 hover:bg-zinc-800/60 border border-white/5 p-6 rounded-[2rem] flex items-center justify-between transition-all"
                >
                  <div className="flex items-center gap-6">
                    <div className="w-14 h-14 bg-black/60 rounded-2xl flex items-center justify-center text-cyan-400 border border-white/5 group-hover:border-cyan-500/50 transition-colors">
                      {categoryIcons[task.category]}
                    </div>
                    <div>
                      <h4 className="text-lg font-bold tracking-tight text-white/90">
                        {task.title}
                      </h4>
                      <div className="flex items-center gap-3 mt-2">
                        <span className="text-[10px] font-black text-zinc-500 uppercase tracking-widest">
                          {task.category}
                        </span>
                        <span
                          className={`text-[9px] font-black px-3 py-0.5 rounded-full ${
                            task.difficulty === "Hard"
                              ? "bg-red-500/20 text-red-500"
                              : task.difficulty === "Medium"
                              ? "bg-yellow-500/20 text-yellow-500"
                              : "bg-green-500/20 text-green-500"
                          }`}
                        >
                          {task.difficulty}
                        </span>
                        <span className="text-[10px] font-black text-cyan-400 italic">
                          +{task.xp} XP
                        </span>
                      </div>
                    </div>
                  </div>
                  <button
                    onClick={() =>
                      setPlayer((prev) => ({
                        ...prev,
                        xp: prev.xp + task.xp,
                        tasks: prev.tasks.filter((t) => t.id !== task.id),
                        streak: prev.streak + 1,
                      }))
                    }
                    className="w-14 h-14 bg-cyan-500/10 text-cyan-400 rounded-2xl flex items-center justify-center hover:bg-cyan-500 hover:text-black transition-all shadow-lg"
                  >
                    <ShieldCheck size={28} />
                  </button>
                </motion.div>
              ))}
            </AnimatePresence>
          </div>

          {/* REGALIAS REFORMULADAS */}
          <section className="bg-zinc-900/20 p-8 rounded-[2.5rem] border border-white/5">
            <h3 className="text-xs font-black uppercase tracking-widest mb-6 text-pink-500 flex items-center gap-3">
              <Gift size={18} /> Baú de Recompensas
            </h3>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              {player.inventory?.map((perk) => (
                <button
                  key={perk.id}
                  onClick={() =>
                    setPlayer((p) => ({
                      ...p,
                      inventory: p.inventory.filter((i) => i.id !== perk.id),
                    }))
                  }
                  className="p-4 bg-black/40 border border-white/5 rounded-2xl hover:border-pink-500/50 transition-all text-[10px] font-black uppercase text-white/70 hover:text-pink-500"
                >
                  {perk.name}
                </button>
              ))}
              {player.inventory?.length === 0 && (
                <p className="col-span-full text-center py-4 text-xs font-bold text-zinc-600 italic uppercase">
                  O baú está vazio. Suba de nível para encontrar tesouros.
                </p>
              )}
            </div>
          </section>
        </main>
      </div>
    </div>
  );
}
