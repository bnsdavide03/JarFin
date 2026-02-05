\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{jarfin}[2026/02/05 JarFin Thesis Class]

% ---------- Classe base ----------
\LoadClass[12pt,a4paper,oneside]{report}

% ---------- Pacchetti obbligatori ----------
\RequirePackage[a4paper, margin=3cm]{geometry}
\RequirePackage{setspace}
\RequirePackage{graphicx}
\RequirePackage{subcaption}
\RequirePackage{amsmath,amssymb}
\RequirePackage{fancyhdr}
\RequirePackage{titlesec}
\RequirePackage{float}
\RequirePackage{booktabs}
\RequirePackage{array}
\RequirePackage{listings}
\RequirePackage[ruled,vlined]{algorithm2e}

\usepackage{xcolor}
\definecolor{primaryBlue}{RGB}{10, 55, 100}
\definecolor{accentGold}{RGB}{200, 150, 50}
\definecolor{codeBg}{RGB}{245, 245, 245}

% ---------- Spaziatura ----------
\onehalfspacing
\setlength{\parindent}{0pt}
\setlength{\parskip}{4pt}

% ---------- Header / Footer ----------
\pagestyle{fancy}
\fancyhf{}

\renewcommand{\headrulewidth}{0pt} 

\fancyfoot[C]{\thepage}

% ---------- Formato capitoli ----------
\titleformat{\chapter}[display]
{\normalfont\huge\bfseries\color{black}}
{\chaptertitlename\ \thechapter}{20pt}{\Huge}

% ---------- Listings ----------
\lstset{
    backgroundcolor=\color{codeBg},
    basicstyle=\ttfamily\small,
    frame=single,
    breaklines=true,
    numbers=left,
    numberstyle=\tiny,
    keywordstyle=\color{black}\bfseries
}

% ---------- Fix Compatibilità ----------
\providecommand{\textLaTeX}{\LaTeX}

% ---------- Hyperref ----------
\RequirePackage{hyperref}

\hypersetup{
    colorlinks=true,
    linkcolor=black,
    citecolor=black,
    urlcolor=primaryBlue
}

\endinput