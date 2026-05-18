import { useState } from "react";
import authApi from "../services/authApi";
import { motion, AnimatePresence } from "framer-motion";
import { GoogleLogin } from "@react-oauth/google";
import {
  Mail,
  Lock,
  User,
  Calendar,
  Eye,
  EyeOff,
  Shield,
  Flame,
  Zap,
  CheckCircle2,
  XCircle,
  AlertCircle,
} from "lucide-react";

// ─── Partículas fixas ─────────────────────────────────────────────────────────
const PARTICLES = [...Array(22)].map((_, i) => ({
  id: i,
  left: `${(i * 4.7 + 3) % 100}%`,
  top: `${(i * 7.3 + 5) % 100}%`,
  duration: 3 + (i % 3),
  delay: (i * 0.4) % 4,
  color: i % 4 === 0 ? "#818cf8" : "#22d3ee",
  size: i % 3 === 0 ? "2px" : "1px",
}));

const ParticleField = () => (
  <div className="absolute inset-0 overflow-hidden pointer-events-none">
    {PARTICLES.map((p) => (
      <motion.div
        key={p.id}
        className="absolute rounded-full"
        style={{ left: p.left, top: p.top, width: p.size, height: p.size, background: p.color }}
        animate={{ opacity: [0, 0.9, 0], scale: [0, 2.5, 0], y: [0, -70] }}
        transition={{ duration: p.duration, repeat: Infinity, delay: p.delay }}
      />
    ))}
  </div>
);

const ScanLine = () => (
  <motion.div
    className="absolute left-0 right-0 h-px pointer-events-none z-10"
    style={{ background: "linear-gradient(to right, transparent, rgba(6,182,212,0.35), transparent)" }}
    animate={{ top: ["0%", "100%"] }}
    transition={{ duration: 5, repeat: Infinity, ease: "linear" }}
  />
);

// ─── Input reutilizável ───────────────────────────────────────────────────────
const InputField = ({
  icon: Icon, label, type = "text", placeholder,
  name, value, onChange, error, success, hint,
}) => {
  const [show, setShow] = useState(false);
  const isPassword = type === "password";

  let borderColor = "rgba(255,255,255,0.1)";
  if (error) borderColor = "rgba(239,68,68,0.55)";
  else if (success) borderColor = "rgba(34,197,94,0.45)";

  return (
    <div className="space-y-1.5">
      <label
        className="text-[11px] font-bold uppercase tracking-[0.22em] flex items-center gap-1.5"
        style={{ color: error ? "#f87171" : "#94a3b8" }}
      >
        <Icon size={11} style={{ color: error ? "#f87171" : "#22d3ee" }} />
        {label}
      </label>

      <div
        className="relative rounded-xl transition-all duration-200"
        style={{
          background: "rgba(0,0,0,0.5)",
          border: `1px solid ${borderColor}`,
          boxShadow: error
            ? "0 0 12px rgba(239,68,68,0.08)"
            : success
            ? "0 0 12px rgba(34,197,94,0.06)"
            : "none",
        }}
      >
        <div className="absolute left-3.5 top-1/2 -translate-y-1/2" style={{ color: "#52525b" }}>
          <Icon size={15} />
        </div>

        <input
          name={name}
          value={value}
          onChange={onChange}
          type={isPassword ? (show ? "text" : "password") : type}
          placeholder={placeholder}
          className="w-full bg-transparent pl-10 py-3.5 text-[13px] font-medium outline-none"
          style={{
            color: "#e4e4e7",
            paddingRight: isPassword || success || error ? "2.8rem" : "0.875rem",
          }}
        />

        {isPassword ? (
          <button
            type="button"
            onClick={() => setShow(!show)}
            className="absolute right-3 top-1/2 -translate-y-1/2 transition-colors"
            style={{ color: "#52525b" }}
            onMouseEnter={(e) => (e.currentTarget.style.color = "#22d3ee")}
            onMouseLeave={(e) => (e.currentTarget.style.color = "#52525b")}
          >
            {show ? <EyeOff size={15} /> : <Eye size={15} />}
          </button>
        ) : success ? (
          <CheckCircle2 size={15} className="absolute right-3.5 top-1/2 -translate-y-1/2" style={{ color: "#4ade80" }} />
        ) : error ? (
          <XCircle size={15} className="absolute right-3.5 top-1/2 -translate-y-1/2" style={{ color: "#f87171" }} />
        ) : null}
      </div>

      <AnimatePresence>
        {error && (
          <motion.p
            initial={{ opacity: 0, y: -4 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0 }}
            className="text-[11px] font-semibold flex items-center gap-1.5 pl-0.5"
            style={{ color: "#f87171" }}
          >
            <AlertCircle size={11} /> {error}
          </motion.p>
        )}
        {!error && hint && (
          <motion.p
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="text-[11px] pl-0.5"
            style={{ color: "#52525b" }}
          >
            {hint}
          </motion.p>
        )}
      </AnimatePresence>
    </div>
  );
};

// ─── Força da senha ───────────────────────────────────────────────────────────
const PasswordStrength = ({ password }) => {
  if (!password) return null;

  const checks = [
    { ok: password.length >= 8, label: "8+ chars" },
    { ok: /[A-Z]/.test(password), label: "Maiúscula" },
    { ok: /[0-9]/.test(password), label: "Número" },
    { ok: /[^A-Za-z0-9]/.test(password), label: "Símbolo" },
  ];
  const strength = checks.filter((c) => c.ok).length;

  const levels = [
    null,
    { bar: "#ef4444", label: "Fraca", color: "#f87171" },
    { bar: "#eab308", label: "Razoável", color: "#facc15" },
    { bar: "#3b82f6", label: "Boa", color: "#60a5fa" },
    { bar: "#22c55e", label: "Forte 🔥", color: "#4ade80" },
  ];
  const level = levels[strength];

  return (
    <div className="space-y-2 px-0.5">
      <div className="flex gap-1.5">
        {[1, 2, 3, 4].map((i) => (
          <motion.div
            key={i}
            className="h-1 flex-1 rounded-full"
            animate={{ backgroundColor: i <= strength ? level?.bar : "rgba(255,255,255,0.07)" }}
            transition={{ duration: 0.3 }}
          />
        ))}
      </div>
      <div className="flex items-center justify-between">
        {level && (
          <span className="text-[11px] font-bold" style={{ color: level.color }}>
            Senha {level.label}
          </span>
        )}
        <div className="flex gap-2 ml-auto">
          {checks.map((c) => (
            <span
              key={c.label}
              className="text-[9px] font-bold uppercase tracking-wide"
              style={{ color: c.ok ? "#4ade80" : "#3f3f46" }}
            >
              {c.ok ? "✓" : "·"} {c.label}
            </span>
          ))}
        </div>
      </div>
    </div>
  );
};

// ─── Componente principal ─────────────────────────────────────────────────────
export default function Login({ onLogin }) {
  const [mode, setMode] = useState("login");
  const [loading, setLoading] = useState(false);
  const [fields, setFields] = useState({
    name: "", birthdate: "", email: "", password: "", confirmPassword: "",
  });
  const [errors, setErrors] = useState({});

  const set = (key) => (e) => {
    setFields((f) => ({ ...f, [key]: e.target.value }));
    setErrors((err) => ({ ...err, [key]: "" }));
  };

  const validate = () => {
    const e = {};
    if (mode === "register") {
      if (!fields.name.trim()) e.name = "Escolha um nome de herói";
      if (!fields.birthdate) e.birthdate = "Informe sua data de nascimento";
      if (fields.password.length < 6) e.password = "Mínimo de 6 caracteres";
      if (!fields.confirmPassword) e.confirmPassword = "Confirme sua senha";
      else if (fields.password !== fields.confirmPassword)
        e.confirmPassword = "As senhas não coincidem";
    }
    if (!fields.email.includes("@")) e.email = "E-mail inválido";
    if (!fields.password) e.password = "Informe sua senha";
    return e;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length > 0) { setErrors(errs); return; }
    setLoading(true);

    try{
        const endpoint = mode === "register" ? "/api/auth/register" : "/api/auth/login";
        const body = mode === "register"
            ? {name: fields.name, birthdate: fields.birthdate, email: fields.email, password: fields.password}
            : {email: fields.email, password: fields.password};

        const res = await authApi.post(endpoint, body);

        localStorage.setItem("token", res.data.token);
        localStorage.setItem("userId", String(res.data.userId));
        if (onLogin) onLogin(res.data.userId);

    } catch (err){
        const msg = err.response?.data?.message || err.message || "Erro ao autenticar";
        // Show the error on the most relevant field. 401/404 are credential
        // problems; 409 is duplicate email on register; everything else is
        // generic so we put it on the email field where the UI already shows it.
        const status = err.response?.status;
        if (status === 401 || status === 404) {
            setErrors({ password: status === 404 ? "E-mail não cadastrado" : "Senha incorreta" });
        } else if (status === 409) {
            setErrors({ email: "Este e-mail já está cadastrado" });
        } else {
            setErrors({ email: msg });
        }
    } finally {
        setLoading(false);
    }

  };

  const switchMode = (m) => {
    setMode(m);
    setErrors({});
    setFields({ name: "", birthdate: "", email: "", password: "", confirmPassword: "" });
  };

  const handleGoogleSuccess = async (credentialResponse) => {
    setLoading(true);
    try {
      const res = await authApi.post("/api/auth/google", {
        idToken: credentialResponse.credential,
      });
      localStorage.setItem("token", res.data.token);
      localStorage.setItem("userId", String(res.data.userId));
      if (onLogin) onLogin(res.data.userId);
    } catch (err) {
      const msg = err.response?.data?.message || err.message || "Erro ao autenticar com Google";
      setErrors({ email: msg });
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleError = () => {
    setErrors({ email: "Falha ao conectar com o Google. Tente novamente." });
  };

  const passwordsMatch =
    fields.confirmPassword.length > 0 && fields.password === fields.confirmPassword;

  return (
    <div
      className="min-h-screen flex items-center justify-center p-4 relative overflow-hidden"
      style={{ background: "#030303", fontFamily: "'Inter', sans-serif" }}
    >
      {/* Fundo */}
      <div className="absolute inset-0" style={{ background: "radial-gradient(ellipse 80% 50% at top left, rgba(6,182,212,0.07) 0%, transparent 70%)" }} />
      <div className="absolute inset-0" style={{ background: "radial-gradient(ellipse 70% 50% at bottom right, rgba(99,102,241,0.07) 0%, transparent 70%)" }} />
      <div className="absolute inset-0" style={{
        backgroundImage: "linear-gradient(rgba(255,255,255,0.013) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,0.013) 1px, transparent 1px)",
        backgroundSize: "44px 44px",
      }} />
      <ParticleField />

      {/* Bordas laterais */}
      <div className="absolute left-0 top-0 bottom-0 w-px" style={{ background: "linear-gradient(to bottom, transparent, rgba(6,182,212,0.25), transparent)" }} />
      <div className="absolute right-0 top-0 bottom-0 w-px" style={{ background: "linear-gradient(to bottom, transparent, rgba(99,102,241,0.25), transparent)" }} />

      {/* Cantos decorativos */}
      {[["top-5 left-5", "border-l-2 border-t-2"], ["top-5 right-5", "border-r-2 border-t-2"],
        ["bottom-5 left-5", "border-l-2 border-b-2"], ["bottom-5 right-5", "border-r-2 border-b-2"]
      ].map(([pos, border], i) => (
        <div key={i} className={`absolute ${pos} w-7 h-7 ${border}`} style={{ borderColor: "rgba(6,182,212,0.25)" }} />
      ))}

      <div className="w-full max-w-sm relative">

        {/* Logo */}
        <motion.div
          initial={{ opacity: 0, y: -18 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
          className="text-center mb-7"
        >
          <div
            className="inline-flex items-center justify-center w-20 h-20 rounded-2xl mb-4 relative"
            style={{
              background: "linear-gradient(135deg, rgba(6,182,212,0.15), rgba(99,102,241,0.15))",
              border: "1px solid rgba(6,182,212,0.3)",
              boxShadow: "0 0 30px rgba(6,182,212,0.12)",
            }}
          >
            <img
              src="/logo.png"
              alt="HeroMode"
              className="w-14 h-14 object-contain"
              style={{ filter: "drop-shadow(0 0 12px rgba(34,211,238,0.5))" }}
            />
            <div
              className="absolute -top-1 -right-1 w-3 h-3 rounded-full animate-pulse"
              style={{ background: "#22d3ee", boxShadow: "0 0 8px rgba(34,211,238,0.8)" }}
            />
          </div>
          <h1 className="text-[26px] font-black italic tracking-tight" style={{ color: "#f4f4f5" }}>
            HERO<span style={{ color: "#22d3ee" }}>QUEST</span>
          </h1>
          <p className="text-[11px] font-semibold uppercase tracking-[0.35em] mt-1" style={{ color: "#52525b" }}>
            Sistema de Progressão
          </p>
        </motion.div>

        {/* Card principal */}
        <motion.div
          initial={{ opacity: 0, scale: 0.95, y: 14 }}
          animate={{ opacity: 1, scale: 1, y: 0 }}
          transition={{ duration: 0.45, delay: 0.1 }}
          className="rounded-[1.8rem] overflow-hidden relative"
          style={{
            background: "rgba(14,14,18,0.93)",
            backdropFilter: "blur(30px)",
            border: "1px solid rgba(255,255,255,0.07)",
            boxShadow: "0 0 80px rgba(6,182,212,0.06), 0 30px 60px rgba(0,0,0,0.8)",
          }}
        >
          <ScanLine />
          <div className="h-px" style={{ background: "linear-gradient(to right, transparent, rgba(6,182,212,0.6), transparent)" }} />

          {/* Toggle */}
          <div className="p-5 pb-0">
            <div
              className="rounded-xl p-1 grid grid-cols-2 gap-1"
              style={{ background: "rgba(0,0,0,0.6)", border: "1px solid rgba(255,255,255,0.05)" }}
            >
              {["login", "register"].map((m) => (
                <button
                  key={m}
                  onClick={() => switchMode(m)}
                  className="py-2.5 rounded-lg text-[11px] font-black uppercase tracking-[0.18em] transition-all duration-300"
                  style={
                    mode === m
                      ? { background: "linear-gradient(135deg,#06b6d4,#22d3ee)", color: "#000", boxShadow: "0 0 20px rgba(6,182,212,0.35)" }
                      : { color: "#52525b" }
                  }
                  onMouseEnter={(e) => { if (mode !== m) e.currentTarget.style.color = "#a1a1aa"; }}
                  onMouseLeave={(e) => { if (mode !== m) e.currentTarget.style.color = "#52525b"; }}
                >
                  {m === "login" ? "⚔ Entrar" : "✦ Cadastrar"}
                </button>
              ))}
            </div>
          </div>

          {/* Formulário */}
          <div className="p-5">
            <AnimatePresence mode="wait">
              <motion.form
                key={mode}
                onSubmit={handleSubmit}
                initial={{ opacity: 0, x: mode === "login" ? -16 : 16 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: mode === "login" ? 16 : -16 }}
                transition={{ duration: 0.22 }}
                className="space-y-3.5"
              >
                {/* Campos só do cadastro */}
                {mode === "register" && (
                  <>
                    <InputField
                      icon={User} label="Nome de Herói" name="name"
                      placeholder="Como quer ser chamado?"
                      value={fields.name} onChange={set("name")}
                      error={errors.name}
                      success={fields.name.trim().length > 2 && !errors.name}
                    />
                    <InputField
                      icon={Calendar} label="Data de Nascimento" type="date"
                      name="birthdate" value={fields.birthdate} onChange={set("birthdate")}
                      error={errors.birthdate}
                      success={!!fields.birthdate && !errors.birthdate}
                    />
                  </>
                )}

                {/* E-mail */}
                <InputField
                  icon={Mail} label="E-mail" type="email" name="email"
                  placeholder="seu@email.com"
                  value={fields.email} onChange={set("email")}
                  error={errors.email}
                  success={fields.email.includes("@") && !errors.email}
                />

                {/* Senha */}
                <div className="space-y-2">
                  <InputField
                    icon={Lock} label="Senha" type="password" name="password"
                    placeholder={mode === "register" ? "Mínimo 6 caracteres" : "••••••••"}
                    value={fields.password} onChange={set("password")}
                    error={errors.password}
                  />
                  {mode === "register" && <PasswordStrength password={fields.password} />}
                </div>

                {/* Confirmar senha — só no cadastro */}
                {mode === "register" && (
                  <InputField
                    icon={Lock} label="Confirmar Senha" type="password" name="confirmPassword"
                    placeholder="Repita sua senha"
                    value={fields.confirmPassword} onChange={set("confirmPassword")}
                    error={errors.confirmPassword}
                    success={passwordsMatch}
                    hint={!fields.confirmPassword ? "Digite a senha novamente para confirmar" : undefined}
                  />
                )}

                {/* Esqueceu senha — só no login */}
                {mode === "login" && (
                  <div className="flex justify-between items-center pt-0.5">
                    <span className="text-[12px] font-medium" style={{ color: "#52525b" }}>
                      Bem-vindo de volta, herói.
                    </span>
                    <button
                      type="button"
                      className="text-[12px] font-semibold transition-colors"
                      style={{ color: "#71717a" }}
                      onMouseEnter={(e) => (e.currentTarget.style.color = "#22d3ee")}
                      onMouseLeave={(e) => (e.currentTarget.style.color = "#71717a")}
                    >
                      Esqueceu a senha?
                    </button>
                  </div>
                )}

                {/* Botão submit */}
                <motion.button
                  type="submit"
                  disabled={loading}
                  whileTap={{ scale: 0.97 }}
                  className="w-full relative font-black text-[12px] uppercase tracking-[0.28em] py-4 rounded-xl overflow-hidden group disabled:opacity-60 transition-all mt-1"
                  style={{
                    background: "linear-gradient(135deg,#06b6d4,#6366f1)",
                    boxShadow: "0 0 35px rgba(6,182,212,0.3)",
                    color: "#000",
                  }}
                >
                  <div className="absolute inset-0 bg-white/15 translate-y-full group-hover:translate-y-0 transition-transform duration-300" />
                  <span className="relative flex items-center justify-center gap-2">
                    {loading ? (
                      <motion.div
                        animate={{ rotate: 360 }}
                        transition={{ duration: 0.8, repeat: Infinity, ease: "linear" }}
                      >
                        <Zap size={17} />
                      </motion.div>
                    ) : (
                      <>
                        <Shield size={14} />
                        {mode === "login" ? "Iniciar Jornada" : "Criar Herói"}
                      </>
                    )}
                  </span>
                </motion.button>

                {/* Divisor */}
                <div className="flex items-center gap-3 py-0.5">
                  <div className="flex-1 h-px" style={{ background: "rgba(255,255,255,0.06)" }} />
                  <span className="text-[11px] font-semibold uppercase tracking-widest" style={{ color: "#3f3f46" }}>ou</span>
                  <div className="flex-1 h-px" style={{ background: "rgba(255,255,255,0.06)" }} />
                </div>

                {/* Google login — uses the official @react-oauth/google
                    GoogleLogin component which returns an ID token in the
                    onSuccess callback. We forward the credential straight to
                    POST /api/auth/google where the backend verifies it against
                    Google's public keys. */}
                <div className="w-full flex items-center justify-center"
                     style={{ colorScheme: "dark" }}>
                  <GoogleLogin
                    onSuccess={handleGoogleSuccess}
                    onError={handleGoogleError}
                    theme="filled_black"
                    size="large"
                    text={mode === "register" ? "signup_with" : "continue_with"}
                    shape="pill"
                    width="320"
                    locale="pt-BR"
                  />
                </div>

              </motion.form>
            </AnimatePresence>
          </div>

          {/* Linha inferior */}
          <div className="h-px" style={{ background: "linear-gradient(to right, transparent, rgba(99,102,241,0.35), transparent)" }} />

          {/* Rodapé */}
          <div className="px-6 py-4 text-center">
            <p className="text-[12px] font-medium" style={{ color: "#52525b" }}>
              {mode === "login" ? "Novo por aqui? " : "Já tem uma conta? "}
              <button
                onClick={() => switchMode(mode === "login" ? "register" : "login")}
                className="font-bold transition-colors"
                style={{ color: "#22d3ee" }}
                onMouseEnter={(e) => (e.currentTarget.style.color = "#67e8f9")}
                onMouseLeave={(e) => (e.currentTarget.style.color = "#22d3ee")}
              >
                {mode === "login" ? "Crie seu herói" : "Fazer login"}
              </button>
            </p>
          </div>
        </motion.div>

        {/* Status online */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.65 }}
          className="flex items-center justify-center gap-2 mt-5"
        >
          <div
            className="w-1.5 h-1.5 rounded-full animate-pulse"
            style={{ background: "#4ade80", boxShadow: "0 0 8px rgba(74,222,128,0.7)" }}
          />
          <span className="text-[10px] font-semibold uppercase tracking-[0.3em]" style={{ color: "#3f3f46" }}>
            Servidor Online
          </span>
          <Flame size={11} className="text-orange-500" />
        </motion.div>
        //Apenas um comentário para ver se o
        //commit vai fazer o deploy na vercell
        //Render também precisa funcionar, é o mais lento da parada
        //Aiven ta rodando
      </div>
    </div>
  );
}
