#!/usr/bin/env python3
"""
Modern Machines - ICM Pipeline CLI Helper

Provides mechanical automation for the Interpretable Context Methodology (ICM)
workflow in Modern Machines. Handles status inspection, stage validation,
asset triggers, Gradle compilation checks, and feature archiving.
"""

import os
import sys
import shutil
import argparse
import subprocess
from datetime import datetime
from pathlib import Path

# Paths
PIPELINE_ROOT = Path(__file__).resolve().parent
PROJECT_ROOT = PIPELINE_ROOT.parent
STAGES_DIR = PIPELINE_ROOT / "stages"
RUNS_DIR = PIPELINE_ROOT / "runs"

STAGES = [
    ("01", "01_design_spec", "output/feature_spec.md"),
    ("02", "02_data_and_assets", "output/asset_and_data_spec.md"),
    ("03", "03_implementation", "output/implementation_summary.md"),
    ("04", "04_verification", "output/verification_report.md"),
]

TEMPLATE_SPEC = """# Feature Specification: {feature_name}

## 1. Executive Summary
- **Feature Name**: {feature_title}
- **Category**: Machine / Modular Part / Material / Tool
- **Tier**: Tier 2 (Electric / Advanced)

---

## 2. Registry Identifiers & Names
- **Block ID**: `modernmachines:{feature_id}`
- **Block Display Name**: {feature_title}
- **Item IDs**:
  - `modernmachines:{feature_id}`
- **BlockEntity ID**: `modernmachines:{feature_id}_be`
- **Menu ID**: `modernmachines:{feature_id}_menu`
- **Recipe Type ID**: `modernmachines:{feature_id}_recipe`

---

## 3. Gameplay Mechanics & Balance
- **Energy Storage**: 50,000 FE
- **Energy Consumption**: 30 FE/t
- **Process Time**: 160 ticks (8.0 seconds)
- **Input Slots**: 1
- **Output Slots**: 1
- **Upgrade Slots**: 4 (Supports Speed, Efficiency, Capacity)
- **Default Side IO**:
  - `TOP`: Input (Item)
  - `BOTTOM`: Output (Item)
  - `BACK`: Input (Energy)
  - `FRONT`: Display / Interacting Face

---

## 4. GUI Layout Specification (Flexbox)
- **Dimensions**: 176 x 166
- **Layout Tree**:
  - `Column`:
    - `Row` (justify: `SPACE_BETWEEN`):
      - `SlotElement(0, 0)` [Input]
      - `ProgressBarElement` [Progress Arrow, atlas uv: (36, 0)]
      - `SlotElement(0, 1)` [Output]
    - `Spacer(10)`
    - `PlayerInventoryElement` [Player 3x9 + 1x9 hotbar]
  - `SideTabElement` [Side Config window toggle]

---

## 5. Recipes & Progression
- **Crafting Recipe**:
  - Shape: 3x3
  - Ingredients: Iron Plates, Electronic Circuit, Steel Casing, Furnace Core
- **Primary Processing Recipe**:
  - Input: Raw Ore -> Output: 2x Crushed Dust (100%), 1x Byproduct Nugget (25%)
"""

def get_stage_files(stage_folder):
    out_dir = STAGES_DIR / stage_folder / "output"
    if not out_dir.exists():
        return []
    return [f for f in out_dir.iterdir() if f.name != ".gitkeep"]

def cmd_status(args):
    print("=" * 65)
    print("  MODERN MACHINES - ICM PIPELINE STATUS")
    print("=" * 65)
    for num, name, target_file in STAGES:
        files = get_stage_files(name)
        target_path = STAGES_DIR / name / target_file
        status_str = "[COMPLETE]" if target_path.exists() else ("[IN PROGRESS]" if files else "[EMPTY]")
        print(f"\nStage {num}: {name} -> {status_str}")
        if files:
            for f in files:
                mtime = datetime.fromtimestamp(f.stat().st_mtime).strftime("%Y-%m-%d %H:%M:%S")
                print(f"   * {f.name} ({f.stat().st_size} bytes, modified: {mtime})")
        else:
            print("   (no working artifacts yet)")
    print("\n" + "=" * 65)

def cmd_new(args):
    feature_name = args.name.lower().replace(" ", "_").replace("-", "_")
    feature_title = args.name.replace("_", " ").title()
    stage1_out = STAGES_DIR / "01_design_spec" / "output" / "feature_spec.md"
    
    if stage1_out.exists() and not args.force:
        print(f"ERROR: Active feature already exists at: {stage1_out}")
        print("Use --force to overwrite or run `archive <name>` first.")
        return 1

    stage1_out.parent.mkdir(parents=True, exist_ok=True)
    content = TEMPLATE_SPEC.format(
        feature_name=feature_name,
        feature_title=feature_title,
        feature_id=feature_name
    )
    with open(stage1_out, "w", encoding="utf-8") as f:
        f.write(content)
    
    print(f"Initialized new feature '{feature_title}' in Stage 01:")
    print(f"  -> {stage1_out}")
    print("Next step: Edit the spec file, review balance/IDs, then proceed to Stage 02.")
    return 0

def cmd_check(args):
    stage_target = args.stage
    print(f"Checking prerequisites for Stage {stage_target}...")

    if stage_target in ["01", "1"]:
        print("[OK] Stage 01 requires only user instructions and Layer 3 references.")
        return 0

    if stage_target in ["02", "2"]:
        spec = STAGES_DIR / "01_design_spec" / "output" / "feature_spec.md"
        if not spec.exists():
            print(f"[FAIL] Missing required input from Stage 01: {spec}")
            return 1
        print(f"[OK] Found approved design spec: {spec}")
        return 0

    if stage_target in ["03", "3"]:
        spec = STAGES_DIR / "01_design_spec" / "output" / "feature_spec.md"
        data_spec = STAGES_DIR / "02_data_and_assets" / "output" / "asset_and_data_spec.md"
        missing = []
        if not spec.exists():
            missing.append(str(spec))
        if not data_spec.exists():
            missing.append(str(data_spec))
        if missing:
            print(f"[FAIL] Missing required inputs: {', '.join(missing)}")
            return 1
        print("[OK] Stage 03 inputs validated (Design Spec & Asset Data Spec present).")
        return 0

    if stage_target in ["04", "4"]:
        impl = STAGES_DIR / "03_implementation" / "output" / "implementation_summary.md"
        if not impl.exists():
            print(f"[FAIL] Missing required input: {impl}")
            return 1
        print("[OK] Stage 04 inputs validated (Implementation Summary present).")
        return 0

    print(f"Unknown stage: {stage_target}. Valid stages: 01, 02, 03, 04")
    return 1

def cmd_assets(args):
    print("Running asset generation pipeline in scripts/...")
    master_script = PROJECT_ROOT / "scripts" / "generate_all_assets.py"
    if not master_script.exists():
        print(f"ERROR: Cannot find {master_script}")
        return 1
    
    result = subprocess.run([sys.executable, str(master_script)], cwd=PROJECT_ROOT)
    if result.returncode == 0:
        print("[SUCCESS] Asset generation completed successfully.")
    else:
        print(f"[FAILED] Asset generation exited with code {result.returncode}")
    return result.returncode

def cmd_build(args):
    print("Executing Gradle compilation check (compileJava)...")
    gradle_cmd = "gradlew.bat" if os.name == "nt" else "./gradlew"
    cmd_path = PROJECT_ROOT / gradle_cmd
    
    if not cmd_path.exists():
        print(f"ERROR: Cannot find {cmd_path}")
        return 1
    
    result = subprocess.run([str(cmd_path), "compileJava"], cwd=PROJECT_ROOT)
    if result.returncode == 0:
        print("\n[SUCCESS] Gradle Java compilation succeeded cleanly.")
    else:
        print(f"\n[FAILED] Compilation failed with exit code {result.returncode}")
    return result.returncode

def cmd_archive(args):
    feature_name = args.name.lower().replace(" ", "_").replace("-", "_")
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    archive_dir = RUNS_DIR / f"{timestamp}_{feature_name}"
    
    print(f"Archiving active feature run to: {archive_dir}...")
    archive_dir.mkdir(parents=True, exist_ok=True)
    
    archived_count = 0
    for num, name, _ in STAGES:
        stage_out = STAGES_DIR / name / "output"
        if stage_out.exists():
            target_sub = archive_dir / name
            target_sub.mkdir(parents=True, exist_ok=True)
            for f in stage_out.iterdir():
                if f.is_file() and f.name != ".gitkeep":
                    shutil.copy2(f, target_sub / f.name)
                    archived_count += 1
                    f.unlink() # Clean active working file

    # Write archive manifest
    manifest_file = archive_dir / "manifest.json"
    manifest_content = (
        f'{{\n  "feature": "{feature_name}",\n  "archived_at": "{datetime.now().isoformat()}",\n'
        f'  "files_archived": {archived_count}\n}}\n'
    )
    with open(manifest_file, "w", encoding="utf-8") as f:
        f.write(manifest_content)

    print(f"[SUCCESS] Archived {archived_count} artifacts to {archive_dir}.")
    print("Active stage output folders have been cleaned and are ready for the next feature.")
    return 0

def main():
    parser = argparse.ArgumentParser(description="Modern Machines ICM Pipeline CLI")
    subparsers = parser.add_subparsers(dest="command", required=True)

    # Status
    p_status = subparsers.add_parser("status", help="Display status of active stages and artifacts")
    p_status.set_defaults(func=cmd_status)

    # New
    p_new = subparsers.add_parser("new", help="Initialize a new feature in Stage 01")
    p_new.add_argument("name", help="Name of the new feature (e.g. crusher, electric_furnace)")
    p_new.add_argument("--force", action="store_true", help="Overwrite existing active spec if present")
    p_new.set_defaults(func=cmd_new)

    # Check
    p_check = subparsers.add_parser("check", help="Validate stage prerequisites against contract")
    p_check.add_argument("stage", help="Stage number (01, 02, 03, 04)")
    p_check.set_defaults(func=cmd_check)

    # Assets
    p_assets = subparsers.add_parser("assets", help="Trigger procedural asset generation scripts")
    p_assets.set_defaults(func=cmd_assets)

    # Build
    p_build = subparsers.add_parser("build", help="Run Gradle compileJava check")
    p_build.set_defaults(func=cmd_build)

    # Archive
    p_archive = subparsers.add_parser("archive", help="Archive active stage outputs to pipeline/runs/")
    p_archive.add_argument("name", help="Name of feature to archive")
    p_archive.set_defaults(func=cmd_archive)

    args = parser.parse_args()
    return args.func(args)

if __name__ == "__main__":
    sys.exit(main() or 0)
